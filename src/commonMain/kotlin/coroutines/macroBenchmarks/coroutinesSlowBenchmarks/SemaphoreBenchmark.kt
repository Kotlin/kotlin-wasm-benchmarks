package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.benchmark.*
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import microBenchmarks.Random
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

private const val WORK_INSIDE = 50
private const val WORK_OUTSIDE = 50
private const val BATCH_SIZE = 100000
private const val COROUTINES = 10_000
private const val MAX_PERMITS = 100000

private fun doGeomDistrWork(random: Random, work: Int) {
    // We use geometric distribution here. We also checked on macbook pro 13" (2017) that the resulting work times
    // are distributed geometrically, see https://github.com/Kotlin/kotlinx.coroutines/pull/1464#discussion_r355705325
    val p = 1.0 / work

//    Used to have ThreadLocal to avoid contention, but there is no multithreading in K/Wasm.
//    The generator is shared by all work calls of one invocation so that consecutive draws form the distribution.
    while (true) {
        if (random.nextDouble(1.0) < p) break
    }
}

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/jvm/kotlin/kotlinx/coroutines/SemaphoreBenchmark.kt
 */
@State(Scope.Benchmark)
open class SemaphoreBenchmark: ParametrizedDispatcherBase() {

    private fun runBenchmark(blackhole: Blackhole, block: suspend () -> Unit) {
        val dispatcher = coroutineContext
        var completed = 0
        suspend {
            val jobs = ArrayList<Job>(COROUTINES)
            repeat(COROUTINES) {
                jobs += launch {
                    repeat(BATCH_SIZE / COROUTINES) {
                        block()
                        completed++
                    }
                }
            }
            jobs.forEach { it.join() }
        }.startCoroutine(Continuation(dispatcher) { it.getOrThrow() })
        dispatcher.drain()
        check(completed == BATCH_SIZE) { "benchmark did not complete: $completed" }
        blackhole.consume(completed)
    }

    @Benchmark
    fun semaphore(blackhole: Blackhole) {
        val random = Random()
        val semaphore = Semaphore(MAX_PERMITS)
        runBenchmark(blackhole) {
            semaphore.withPermit {
                doGeomDistrWork(random, WORK_INSIDE)
            }
            doGeomDistrWork(random, WORK_OUTSIDE)
        }
    }

    @Benchmark
    fun channelAsSemaphore(blackhole: Blackhole) {
        val random = Random()
        val semaphore = Channel<Unit>(MAX_PERMITS)
        runBenchmark(blackhole) {
            semaphore.send(Unit) // acquire
            doGeomDistrWork(random, WORK_INSIDE)
            semaphore.receive() // release
            doGeomDistrWork(random, WORK_OUTSIDE)
        }
    }
}
