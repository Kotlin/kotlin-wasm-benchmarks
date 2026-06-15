package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.benchmark.*
import kotlinx.coroutines.launch
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/benchmarks/src/jmh/kotlin/benchmarks/scheduler/LaunchBenchmark.kt
 *
 * Uses the minimal single-threaded CyclicBarrier below (common code) instead of the JVM/arrow one,
 * so the benchmark is portable across all targets.
 */
@State(Scope.Benchmark)
open class LaunchBenchmark : ParametrizedDispatcherBase() {

    private val jobsToLaunch = 1000
    private val submitters = 40

    private val allLaunched = CyclicBarrier(submitters)
    private val stopBarrier = CyclicBarrier(submitters + 1)

    @Benchmark
    fun massiveLaunch() {
        var done = false
        suspend {
            repeat(submitters) {
                launch {
                    // Wait until all cores are occupied
                    allLaunched.await()

                    (1..jobsToLaunch).map {
                        launch {
                            // do nothing
                        }
                    }.map { it.join() }

                    stopBarrier.await()
                }
            }

            stopBarrier.await()
            done = true
        }.startCoroutine(Continuation(coroutineContext) { it.getOrThrow() })
        coroutineContext.drain()
        check(done) { "benchmark did not complete" }
    }
}

/**
 * Minimal cyclic barrier for the single-threaded cooperative scheduler used by these benchmarks:
 * [await] suspends the caller until [parties] callers are waiting, then all are resumed and the
 * barrier resets for reuse. Single-threaded, so no synchronization is needed.
 */
private class CyclicBarrier(private val parties: Int) {
    private var waiting = mutableListOf<Continuation<Unit>>()

    suspend fun await() {
        if (waiting.size + 1 >= parties) {
            val toResume = waiting
            waiting = mutableListOf()
            toResume.forEach { it.resume(Unit) }
        } else {
            suspendCoroutine<Unit> { cont -> waiting.add(cont) }
        }
    }
}
