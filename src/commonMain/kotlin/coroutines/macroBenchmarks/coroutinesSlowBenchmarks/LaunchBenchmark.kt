package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.benchmark.*
import kotlinx.coroutines.launch
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

private const val JOBS_TO_LAUNCH = 1000
private const val SUBMITTERS = 40

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/benchmarks/src/jmh/kotlin/benchmarks/scheduler/LaunchBenchmark.kt
 *
 * Uses the minimal single-threaded CyclicBarrier below (common code) instead of the JVM/arrow one,
 * so the benchmark is portable across all targets.
 */
@State(Scope.Benchmark)
open class LaunchBenchmark : ParametrizedDispatcherBase() {

    @Benchmark
    fun massiveLaunch(blackhole: Blackhole) {
        val dispatcher = coroutineContext
        val allLaunched = CyclicBarrier(SUBMITTERS)
        val stopBarrier = CyclicBarrier(SUBMITTERS + 1)
        var done = false
        suspend {
            repeat(SUBMITTERS) {
                launch {
                    // Wait until all cores are occupied
                    allLaunched.await()

                    (1..JOBS_TO_LAUNCH).map {
                        launch {
                            // do nothing
                        }
                    }.map { it.join() }

                    stopBarrier.await()
                }
            }

            stopBarrier.await()
            done = true
        }.startCoroutine(Continuation(dispatcher) { it.getOrThrow() })
        dispatcher.drain()
        check(done) { "benchmark did not complete" }
        blackhole.consume(done)
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
