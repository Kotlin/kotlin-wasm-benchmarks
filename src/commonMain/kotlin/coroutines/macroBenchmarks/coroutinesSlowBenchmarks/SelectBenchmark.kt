package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlinx.benchmark.*
import macroBenchmarks.coroutines.ParametrizedDispatcherBase

// 450
private const val ITERATIONS = 100_000
private const val EXPECTED_SUM = ITERATIONS.toLong() * (ITERATIONS - 1) / 2

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/jvm/kotlin/kotlinx/coroutines/channels/SelectBenchmark.kt
 */
@State(Scope.Benchmark)
open class SelectBenchmark: ParametrizedDispatcherBase() {

    @Benchmark
    fun stressSelect(blackhole: Blackhole)  {
        val dispatcher = coroutineContext
        var sum = 0L
        var done = false
        suspend {
            val pingPong = Channel<Int>()
            launch {
                repeat(ITERATIONS) {
                    select {
                        pingPong.onSend(it) {}
                    }
                }
            }

            launch {
                repeat(ITERATIONS) {
                    select {
                        pingPong.onReceive() { sum += it }
                    }
                }
                done = true
            }
        }.startCoroutine(Continuation(dispatcher) { it.getOrThrow() })
        dispatcher.drain()
        check(done) { "benchmark did not complete" }
        check(sum == EXPECTED_SUM) { "Failed: expected $EXPECTED_SUM, got $sum" }
        blackhole.consume(sum)
    }
}
