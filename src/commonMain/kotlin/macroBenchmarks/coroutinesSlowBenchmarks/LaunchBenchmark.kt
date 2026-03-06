package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.launch
import kotlinx.benchmark.*
import arrow.fx.coroutines.CyclicBarrier
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/benchmarks/src/jmh/kotlin/benchmarks/scheduler/LaunchBenchmark.kt
 * Probably, no need of FJP-like adaptation, cause in K/Wasm no multithreading?
 *
 * Used CyclicBarrier from arrow-kt as an alternative to java CyclicBarrier
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