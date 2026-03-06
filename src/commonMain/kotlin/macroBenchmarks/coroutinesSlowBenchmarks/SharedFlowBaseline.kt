package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlinx.benchmark.*

/* Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/main/kotlin/SharedFlowBaseline.kt
 * Stresses out 'synchronized' codepath in MutableSharedFlow
 */
@State(Scope.Benchmark)
open class SharedFlowBaseline : ParametrizedDispatcherBase() {

    private var size: Int = 10_000

    @Benchmark
    fun baseline() {
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
        check(sum == size * (size - 1) / 2) { "benchmark did not complete $sum" }
    }
}