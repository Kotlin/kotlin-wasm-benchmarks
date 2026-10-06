package macroBenchmarks.coroutinesSlowBenchmarks

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.startCoroutine
import kotlinx.benchmark.*
import kotlinx.coroutines.flow.*

const val SIZE = 100_000
const val RESULT_TAKE_WHILE_DIRECT = SIZE / 2

/* Adapted benchmark from kotlinx.coroutines
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/benchmarks/main/kotlin/SharedFlowBaseline.kt
 * Stresses out 'synchronized' codepath in MutableSharedFlow
 */
@State(Scope.Benchmark)
open class SharedFlowBaseline : ParametrizedDispatcherBase() {
    @Benchmark
    fun baseline(blackhole: Blackhole) {
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
        blackhole.consume(sum)
    }

    @Benchmark
    fun takeWhileDirect(blackhole: Blackhole) {
        var result: Int = 0
        suspend {
            (0L..Long.MAX_VALUE).asFlow().takeWhile { it < SIZE }.consume()
        }.startCoroutine(Continuation(coroutineContext) { result = it.getOrThrow() })
        coroutineContext.drain()
        check (result == RESULT_TAKE_WHILE_DIRECT) { "benchmark did not complete: $result" }
        blackhole.consume(result)
    }
}

private suspend inline fun Flow<Long>.consume() =
    filter { it % 2L != 0L }
        .map { it * it }.count()
