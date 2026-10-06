package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.launch
import macroBenchmarks.coroutines.CancellableChannel
import macroBenchmarks.coroutines.CancellableReusableChannel
import macroBenchmarks.coroutines.NonCancellableChannel
import macroBenchmarks.coroutines.ParametrizedDispatcherBaseSlow
import macroBenchmarks.coroutines.SimpleChannel
import kotlin.concurrent.Volatile
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlinx.benchmark.*

private const val ITERATIONS = 200_000

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/jvm/kotlin/kotlinx/coroutines/channels/SimpleChannelBenchmark.kt
 */
abstract class SimpleChannelBenchmark : ParametrizedDispatcherBaseSlow() {

    protected abstract fun makeChannel(): SimpleChannel

    @Volatile
    private var sink: Int = 0

    override fun verifyResult(result: Any) =
        (result is Int) && result == ITERATIONS * (ITERATIONS - 1) / 2

    override fun benchmark(): Any {
        var done = false
        suspend {
            val ch = makeChannel()
            launch {
                repeat(ITERATIONS) { ch.send(it) }
            }

            launch {
                repeat(ITERATIONS) { sink += ch.receive() }
            }
            done = true
        }.startCoroutine(Continuation(coroutineContext) { it.getOrThrow() })
        coroutineContext.drain()
        check(done) { "benchmark did not complete" }
        return sink
    }
}

@State(Scope.Benchmark)
class CancellableChannelBenchmark: SimpleChannelBenchmark() {
    override fun makeChannel() = CancellableChannel()

    @Benchmark
    fun cancellable(blackhole: Blackhole) {
        val result = benchmark()
        blackhole.consume(result)
    }
}

@State(Scope.Benchmark)
class CancellableReusableChannelBenchmark: SimpleChannelBenchmark() {
    override fun makeChannel() = CancellableReusableChannel()

    @Benchmark
    fun cancellableReusable(blackhole: Blackhole) {
        val result = benchmark()
        blackhole.consume(result)
    }
}

@State(Scope.Benchmark)
class NonCancellableChannelBenchmark: SimpleChannelBenchmark() {
    override fun makeChannel() = NonCancellableChannel()

    @Benchmark
    fun nonCancellable(blackhole: Blackhole) {
        val result = benchmark()
        blackhole.consume(result)
    }
}