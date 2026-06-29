package macroBenchmarks.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

class CoroutineStub {
    companion object {
        fun evaluate(c: suspend () -> Unit) {
            c.startCoroutine(object : Continuation<Unit> {
                override val context = EmptyCoroutineContext
                override fun resumeWith(result: Result<Unit>) {
                    result.getOrThrow()
                }
            })
        }
    }
}
