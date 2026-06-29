package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlinx.benchmark.*
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.concurrent.Volatile

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/jvm/kotlin/kotlinx/coroutines/channels/SelectBenchmark.kt
 */
@State(Scope.Benchmark)
open class SelectBenchmark: ParametrizedDispatcherBase() {
    // 450
    private val iterations = 100_000

    @Volatile
    private var sink: Int = 0

    @Benchmark
    fun stressSelect()  {
        sink = 0
        var done = false
        suspend {
            val pingPong = Channel<Int>()
            launch {
                repeat(iterations) {
                    select {
                        pingPong.onSend(it) {}
                    }
                }
            }

            launch {
                repeat(iterations) {
                    select {
                        pingPong.onReceive() { sink += it }
                    }
                }
            }
            done = true
        }.startCoroutine(Continuation(coroutineContext) { it.getOrThrow() })
        coroutineContext.drain()
        check(done && sink == iterations * (iterations - 1) / 2) { "benchmark did not complete $sink" }
    }
}
