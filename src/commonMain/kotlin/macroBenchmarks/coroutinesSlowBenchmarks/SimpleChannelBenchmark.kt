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

/*
 * Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/jvm/kotlin/kotlinx/coroutines/channels/SimpleChannelBenchmark.kt
 */
abstract class SimpleChannelBenchmark : ParametrizedDispatcherBaseSlow() {

    private val iterations = 200_000
    protected abstract fun makeChannel(): SimpleChannel

    @Volatile
    private var sink: Int = 0

    override fun verifyResult(result: Any) =
        (result is Int) && result == iterations * (iterations - 1) / 2

    override fun benchmark(): Any {
        var done = false
        suspend {
            val ch = makeChannel()
            launch {
                repeat(iterations) { ch.send(it) }
            }

            launch {
                repeat(iterations) { sink += ch.receive() }
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
    fun cancellable() = benchmark()
}

@State(Scope.Benchmark)
class CancellableReusableChannelBenchmark: SimpleChannelBenchmark() {
    override fun makeChannel() = CancellableReusableChannel()

    @Benchmark
    fun cancellableReusable() = benchmark()
}

@State(Scope.Benchmark)
class NonCancellableChannelBenchmark: SimpleChannelBenchmark() {
    override fun makeChannel() = NonCancellableChannel()

    @Benchmark
    fun nonCancellable() = benchmark()
}