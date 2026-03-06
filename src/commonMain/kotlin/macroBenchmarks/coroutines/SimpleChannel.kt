package macroBenchmarks.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

abstract class SimpleChannel {
    companion object {
        const val NULL_SURROGATE: Int = -1
    }

    protected var producer: Continuation<Unit>? = null
    protected var enqueuedValue: Int = NULL_SURROGATE
    protected var consumer: Continuation<Int>? = null

    suspend fun send(element: Int) {
        require(element != NULL_SURROGATE)
        if (offer(element)) {
            return
        }

        return suspendSend(element)
    }

    private fun offer(element: Int): Boolean {
        if (consumer == null) {
            return false
        }

        consumer!!.resume(element)
        consumer = null
        return true
    }

    suspend fun receive(): Int {
        // Cached value
        if (enqueuedValue != NULL_SURROGATE) {
            val result = enqueuedValue
            enqueuedValue = NULL_SURROGATE
            producer!!.resume(Unit)
            return result
        }

        return suspendReceive()
    }

    abstract suspend fun suspendReceive(): Int
    abstract suspend fun suspendSend(element: Int)
}

class NonCancellableChannel : SimpleChannel() {
    override suspend fun suspendReceive(): Int = suspendCoroutineUninterceptedOrReturn {
        consumer = it.intercepted()
        COROUTINE_SUSPENDED
    }

    override suspend fun suspendSend(element: Int) = suspendCoroutineUninterceptedOrReturn<Unit> {
        enqueuedValue = element
        producer = it.intercepted()
        COROUTINE_SUSPENDED
    }
}

class CancellableChannel : SimpleChannel() {
    override suspend fun suspendReceive(): Int = suspendCancellableCoroutine {
        consumer = it.intercepted()
        COROUTINE_SUSPENDED
    }

    override suspend fun suspendSend(element: Int) = suspendCancellableCoroutine<Unit> {
        enqueuedValue = element
        producer = it.intercepted()
        COROUTINE_SUSPENDED
    }
}

class CancellableReusableChannel : SimpleChannel() {
    @Suppress("INVISIBLE_REFERENCE")
    override suspend fun suspendReceive(): Int = suspendCancellableCoroutineReusable {
        consumer = it.intercepted()
        COROUTINE_SUSPENDED
    }

    @Suppress("INVISIBLE_REFERENCE")
    override suspend fun suspendSend(element: Int) = suspendCancellableCoroutineReusable<Unit> {
        enqueuedValue = element
        producer = it.intercepted()
        COROUTINE_SUSPENDED
    }
}
