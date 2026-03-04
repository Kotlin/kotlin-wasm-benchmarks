package macroBenchmarks

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

private class DrainableDispatcher : CoroutineDispatcher() {
    private val queue = ArrayDeque<Runnable>()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        queue.addLast(block)
    }

    fun drain() {
        while (true) {
            val task = queue.removeFirstOrNull() ?: break
            task.run()
        }
    }
}

// Stresses out 'synchronized' codepath in MutableSharedFlow
open class SharedFlowBaseline : MacroBenchmark() {
    private var size: Int = 10_000

    override fun verifyResult(result: Any) = (result is Int) && result == size * (size - 1) / 2

    override fun benchmark(): Any {
        val dispatcher = DrainableDispatcher()
        val scope = CoroutineScope(dispatcher)
        var sum = 0
        var done = false
        suspend {
            val flow = MutableSharedFlow<Int>()
            scope.launch {
                repeat(size) { flow.emit(it) }
            }
            flow.take(size).collect { sum += it }
            done = true
        }.startCoroutine(Continuation(dispatcher) { it.getOrThrow() })
        dispatcher.drain()
        check(done) { "benchmark did not complete" }
        return sum
    }
}
