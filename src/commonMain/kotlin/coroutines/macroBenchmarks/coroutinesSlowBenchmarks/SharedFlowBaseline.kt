package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlinx.benchmark.*
import kotlinx.coroutines.flow.*

/* Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/main/kotlin/SharedFlowBaseline.kt
 * Stresses out 'synchronized' codepath in MutableSharedFlow
 */
@State(Scope.Benchmark)
open class SharedFlowBaseline : ParametrizedDispatcherBase() {

    companion object {
        const val SIZE: Int = 100_000
        const val RESULT_TAKE_WHILE_DIRECT = SIZE / 2
    }
    @Benchmark
    fun baseline() {
        var sum = 0
        var done = false
        suspend {
            val flow = MutableSharedFlow<Int>()
            launch {
                repeat(SIZE) { flow.emit(it) }
            }
            flow.take(SIZE).collect { sum += it }
            done = true
        }.startCoroutine(Continuation(coroutineContext) { it.getOrThrow() })
        coroutineContext.drain()
        check(done) { "benchmark did not complete" }
        check(sum == SIZE * (SIZE - 1) / 2) { "benchmark did not complete $sum" }
    }

    @Benchmark
    fun takeWhileDirect() {
        var result: Int = 0
        suspend {
            (0L..Long.MAX_VALUE).asFlow().takeWhile { it < SIZE }.consume()
        }.startCoroutine(Continuation(coroutineContext) { result = it.getOrThrow() })
        coroutineContext.drain()
        check (result == RESULT_TAKE_WHILE_DIRECT) { "benchmark did not complete: $result" }
    }

    private suspend inline fun Flow<Long>.consume() =
        filter { it % 2L != 0L }
            .map { it * it }.count()

}