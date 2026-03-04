package macroBenchmarks.coroutinesFastBenchmarks

import kotlinx.coroutines.launch
import macroBenchmarks.coroutines.CancellableChannel
import macroBenchmarks.coroutines.CancellableReusableChannel
import macroBenchmarks.coroutines.NonCancellableChannel
import macroBenchmarks.coroutines.ParametrizedDispatcherBaseSlow
import macroBenchmarks.coroutines.SimpleChannel
import kotlin.concurrent.Volatile
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

// Stresses out 'synchronized' codepath in MutableSharedFlow
sealed class SimpleChannelBenchmark : ParametrizedDispatcherBaseSlow() {

    private val iterations = 10_000
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

    class CancellableChannelBenchmark: SimpleChannelBenchmark() {
        override fun makeChannel() = CancellableChannel()
    }

    class CancellableReusableChannelBenchmark: SimpleChannelBenchmark() {
        override fun makeChannel() = CancellableReusableChannel()
    }

    class NonCancellableChannelBenchmark: SimpleChannelBenchmark() {
        override fun makeChannel() = NonCancellableChannel()
    }
}
