package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.benchmark.*
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlin.random.Random

private fun doGeomDistrWork(work: Int) {
    // We use geometric distribution here. We also checked on macbook pro 13" (2017) that the resulting work times
    // are distributed geometrically, see https://github.com/Kotlin/kotlinx.coroutines/pull/1464#discussion_r355705325
    val p = 1.0 / work

//    Used to have ThreadLocal to avoid contention, but there is no multithreading in K/Wasm?
//    val r = ThreadLocalRandom.current()
    val r = Random
    while (true) {
        if (r.nextDouble() < p) break
    }
}

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/jvm/kotlin/kotlinx/coroutines/SemaphoreBenchmark.kt
 */
@State(Scope.Benchmark)
open class SemaphoreBenchmark: ParametrizedDispatcherBase() {

// With params both stack-switching and non-stack-switching versions fail
//    @Param("0", "1000")
    var coroutines: Int = 1000

//    @Param("1", "2", "4", "8", "32", "128", "100000")
    var maxPermits: Int = 100000

    private fun <T> runBenchmark(semaphore: T, block: (T) -> (suspend () -> Unit)) {
        suspend {
            val n = BATCH_SIZE / coroutines
            val jobs = ArrayList<Job>(coroutines)
            repeat(coroutines) {
                jobs += GlobalScope.launch {
                    repeat(n) {
                        block(semaphore).invoke()
                    }
                }
            }
            jobs.forEach { it.join() }
        }.startCoroutine(Continuation(coroutineContext) { it.getOrThrow() })
        coroutineContext.drain()
    }

    @Benchmark
    fun semaphore() =
        runBenchmark(Semaphore(maxPermits)) { semaphore ->
            suspend {
                semaphore.withPermit {
                    doGeomDistrWork(WORK_INSIDE)
                }
                doGeomDistrWork(WORK_OUTSIDE)
            }
        }

    @Benchmark
    fun channelAsSemaphore() =
        runBenchmark(Channel<Unit>(maxPermits)) { semaphore ->
            suspend {
                semaphore.send(Unit) // acquire
                doGeomDistrWork(WORK_INSIDE)
                semaphore.receive() // release
                doGeomDistrWork(WORK_OUTSIDE)
            }
        }
}

private const val WORK_INSIDE = 50
private const val WORK_OUTSIDE = 50
private const val BATCH_SIZE = 100000