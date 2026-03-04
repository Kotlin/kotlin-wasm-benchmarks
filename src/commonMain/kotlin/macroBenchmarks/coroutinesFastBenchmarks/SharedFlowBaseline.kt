package macroBenchmarks.coroutinesFastBenchmarks

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import macroBenchmarks.coroutines.ParametrizedDispatcherBaseSlow
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine

// Stresses out 'synchronized' codepath in MutableSharedFlow
open class SharedFlowBaseline : ParametrizedDispatcherBaseSlow() {

    private var size: Int = 10_000

    override fun verifyResult(result: Any) = (result is Int) && result == size * (size - 1) / 2

    override fun benchmark(): Any {
        var sum = 0
        var done = false
        suspend {
            val flow = MutableSharedFlow<Int>()
            launch {
                repeat(size) { flow.emit(it) }
            }
            flow.take(size).collect { sum += it }
            done = true
        }.startCoroutine(Continuation(coroutineContext) { it.getOrThrow() })
        coroutineContext.drain()
        check(done) { "benchmark did not complete" }
        return sum
    }
}
