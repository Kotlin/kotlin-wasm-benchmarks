package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.launch
import kotlinx.benchmark.*
import arrow.fx.coroutines.CyclicBarrier
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

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