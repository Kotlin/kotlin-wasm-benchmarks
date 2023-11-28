package macroBenchmarks.coroutines

import kotlin.coroutines.*

abstract class AbstractGenerator<T> {
    private var generatorContinuation: Continuation<Unit>? = null
    private var callerContinuation: Continuation<T>? = null

    fun resetGenerator() {
        CoroutineStub.evaluate(this::initGenerator)
    }

    protected suspend fun yieldValue(x: T) {
        suspendCoroutine { continuation ->
            generatorContinuation = continuation
            callerContinuation?.resume(x)
        }
    }

    private suspend fun initGenerator() {
        suspendCoroutine { continuation ->
            generatorContinuation = continuation
        }

        generatorBody()

        generatorContinuation = null
    }

    protected abstract suspend fun generatorBody()

    fun hasNext(): Boolean {
        return generatorContinuation != null
    }

    suspend fun nextValue(): T {
        return suspendCoroutine { continuation ->
            callerContinuation = continuation
            generatorContinuation?.resume(Unit)
        }
    }
}
