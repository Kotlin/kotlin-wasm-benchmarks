package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.launch
import macroBenchmarks.coroutines.CancellableChannel
import macroBenchmarks.coroutines.CancellableReusableChannel
import macroBenchmarks.coroutines.NonCancellableChannel
import macroBenchmarks.coroutines.ParametrizedDispatcherBaseSlow
import macroBenchmarks.coroutines.SimpleChannel
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlinx.benchmark.*

private const val ITERATIONS = 200_000
private const val EXPECTED_SUM = ITERATIONS.toLong() * (ITERATIONS - 1) / 2

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/jvm/kotlin/kotlinx/coroutines/channels/SimpleChannelBenchmark.kt
 */
abstract class SimpleChannelBenchmark : ParametrizedDispatcherBaseSlow() {

    protected abstract fun makeChannel(): SimpleChannel

    override fun verifyResult(result: Any) =
        (result is Long) && result == EXPECTED_SUM

    override fun benchmark(): Any {
        val dispatcher = coroutineContext
        var sum = 0L
        var done = false
        suspend {
            val ch = makeChannel()
            launch {
                repeat(ITERATIONS) { ch.send(it) }
            }

            launch {
                repeat(ITERATIONS) { sum += ch.receive() }
                done = true
            }
        }.startCoroutine(Continuation(dispatcher) { it.getOrThrow() })
        dispatcher.drain()
        check(done) { "benchmark did not complete" }
        return sum
    }

    protected fun runAndVerify(blackhole: Blackhole) {
        val result = benchmark()
        check(verifyResult(result)) { "Failed: expected $EXPECTED_SUM, got $result" }
        blackhole.consume(result)
    }
}

@State(Scope.Benchmark)
class CancellableChannelBenchmark: SimpleChannelBenchmark() {
    override fun makeChannel() = CancellableChannel()

    @Benchmark
    fun cancellable(blackhole: Blackhole) {
        runAndVerify(blackhole)
    }
}

@State(Scope.Benchmark)
class CancellableReusableChannelBenchmark: SimpleChannelBenchmark() {
    override fun makeChannel() = CancellableReusableChannel()

    @Benchmark
    fun cancellableReusable(blackhole: Blackhole) {
        runAndVerify(blackhole)
    }
}

@State(Scope.Benchmark)
class NonCancellableChannelBenchmark: SimpleChannelBenchmark() {
    override fun makeChannel() = NonCancellableChannel()

    @Benchmark
    fun nonCancellable(blackhole: Blackhole) {
        runAndVerify(blackhole)
    }
}