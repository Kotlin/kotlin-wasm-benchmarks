package microBenchmarks

import kotlinx.benchmark.*
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

@State(Scope.Benchmark)
open class SuspensionsBenchmark : ParametrizedDispatcherBase() {

    val coroutines: MutableList<Continuation<Unit>> = mutableListOf()
    val coroutines2: MutableList<Continuation<Unit>> = mutableListOf()
    var deepCoroutine: Continuation<Unit>? = null

    var deepReached = 0
    var deepCompletion = 0
    var deepSuspensionsCount = 0

    class ManualContinuation : Continuation<Unit> {
        override val context: CoroutineContext = EmptyCoroutineContext
        var completed = false
        override fun resumeWith(result: Result<Unit>) {
            result.getOrThrow()
            completed = true
        }
    }

    fun builder(c: suspend () -> Unit): ManualContinuation {
        val cont = ManualContinuation()
        c.startCoroutine(cont)
        return cont
    }

    private suspend fun deepChain(n: Int) {
        if (n == 0) {
            ++deepReached
        } else {
            suspendCoroutine<Unit> { cont ->
                coroutines2.add(cont)
                coroutines[n - 1].resume(Unit)
                COROUTINE_SUSPENDED
            }
        }
        ++deepCompletion
    }

    @Benchmark
    fun chainSuspensions() {
        coroutines.clear()
        coroutines2.clear()
        deepReached = 0
        deepCompletion = 0
        val depth = 20
        for (i in 1..depth) {
            builder {
                suspendCoroutine { cont ->
                    coroutines.add(cont)
                    COROUTINE_SUSPENDED
                }
                deepChain(i - 1)
            }
        }
        coroutines.last().resume(Unit)
        coroutines2.forEach { it.resume(Unit) }

        check(deepReached == 1) { "deepReached=$deepReached" }
        check(deepCompletion == depth) { "deepCompletion=$deepCompletion" }
    }

    suspend fun deepDeep(n: Int, deepSuspensionsNum: Int) {
        if (n == 0) {
            ++deepReached
            for (i in 1..deepSuspensionsNum) {
                suspendCoroutine<Unit> { cont ->
                    ++deepSuspensionsCount
                    deepCoroutine = cont
                    COROUTINE_SUSPENDED
                }
            }
        } else {
            suspendCoroutine<Unit> { cont ->
                coroutines2.add(cont)
                coroutines[n - 1].resume(Unit)
                COROUTINE_SUSPENDED
            }
        }
        ++deepCompletion
    }

    @Benchmark
    fun deepSuspensions() {
        coroutines.clear()
        coroutines2.clear()
        deepCoroutine = null
        deepReached = 0
        deepCompletion = 0
        deepSuspensionsCount = 0
        val depth = 20
        val deepSuspensionsNum = 20
        for (i in 1..depth) {
            builder {
                suspendCoroutine { cont ->
                    coroutines.add(cont)
                    COROUTINE_SUSPENDED
                }
                deepDeep(i - 1, deepSuspensionsNum)
            }
        }
        coroutines.last().resume(Unit)
        for (i in 1..deepSuspensionsNum) {
            deepCoroutine!!.resume(Unit)
        }
        coroutines2.forEach { it.resume(Unit) }

        check(deepSuspensionsCount == deepSuspensionsNum) { "deepSuspensionsCount=$deepSuspensionsCount" }
        check(deepReached == 1) { "deepReached=$deepReached" }
        check(deepCompletion == depth) { "deepCompletion=$deepCompletion" }
    }


    private suspend fun suspendWithIncrement(value: Int): Int =
        kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn { x ->
            x.resume(value + 1)
            COROUTINE_SUSPENDED
        }

    @Benchmark
    fun multipleSuspensions1() {

        var result = 0

        builder {
            var acc = 0
            for (i in 1..50) {
                acc = suspendWithIncrement(acc)
            }
            result = acc
        }

        check(result == 50) { "result=$result" }
    }

    var resumeCoroutine: (() -> Unit)? = null

    private suspend fun suspendWithIncrement2(value: Int): Int =
        kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn { x ->
            resumeCoroutine = {
                x.resume(value + 1)
            }
            COROUTINE_SUSPENDED
        }

    @Benchmark
    fun multipleSuspensions2() {
        resumeCoroutine = null
        var result = 0
        var acc = 0

        builder {
            for (i in 1..50) {
                acc = suspendWithIncrement2(acc)
            }
            result = acc
        }

        for (i in 1..50) {
            resumeCoroutine!!.invoke()
            check (acc == i) { "Failed: expected $i, got $acc" }
        }

        check (result == 50) { "Failed: expected 50, got $result" }
    }

    @Benchmark
    fun sequence() {
        val iterations = 20
        val fibonacci = sequence {
            yield(1)
            yield(1)
            var a = 1
            var b = 1
            while (true) {
                yield(a + b)
                val temp = a
                a = b
                b += temp
            }
        }.iterator()
        var current = 0
        for (i in 1..iterations) {
            current = fibonacci.next()
        }
        check(current == 6765) { "Failed: expected 6765, got $current" }
    }

    var completionCount = 0

    suspend fun simpleCoroutine(): String {
        return "OK"
    }

    @Benchmark
    fun startCoroutine() {
        completionCount = 0

        // Start multiple coroutines to measure startCoroutine performance
        repeat(100) {
            val coroutine: suspend () -> String = ::simpleCoroutine

            coroutine.startCoroutine(object : Continuation<String> {
                override val context: CoroutineContext
                    get() = EmptyCoroutineContext

                override fun resumeWith(result: Result<String>) {
                    if (result.isSuccess && result.getOrNull() == "OK") {
                        completionCount++
                    }
                }
            })
        }
    }


    var resumeCount = 0

    private suspend fun suspendAndResume(): String =
        kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn { continuation ->
            // Immediately resume to measure the suspension/resumption overhead
            continuation.resume("OK")
            COROUTINE_SUSPENDED
        }

    @Benchmark
    fun suspendCoroutineUninterceptedOrReturn() {
        resumeCount = 0
        var result = "FAIL"

        // Perform multiple suspend/resume cycles
        val coroutine: suspend () -> String = {
            var accumulated = ""
            repeat(10) {
                val value = suspendAndResume()
                if (value == "OK") {
                    resumeCount++
                }
                accumulated = value
            }
            accumulated
        }

        coroutine.startCoroutine(object : Continuation<String> {
            override val context: CoroutineContext
                get() = EmptyCoroutineContext

            override fun resumeWith(value: Result<String>) {
                result = value.getOrNull() ?: "FAIL"
            }
        })

        check (resumeCount == 10 && result == "OK") { "FAIL: $resumeCount" }
    }
}