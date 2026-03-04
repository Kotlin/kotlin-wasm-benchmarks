package macroBenchmarks.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Runnable
import macroBenchmarks.MacroBenchmark
import kotlin.coroutines.CoroutineContext

class DrainableDispatcher : CoroutineDispatcher() {
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

abstract class ParametrizedDispatcherBase : CoroutineScope {

    override val coroutineContext = DrainableDispatcher()
}

abstract class ParametrizedDispatcherBaseSlow : MacroBenchmark(), CoroutineScope {

    override val coroutineContext = DrainableDispatcher()
}
