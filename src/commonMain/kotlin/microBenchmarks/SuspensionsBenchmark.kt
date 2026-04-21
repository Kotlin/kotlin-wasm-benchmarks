package microBenchmarks

import kotlinx.benchmark.*
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.*
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

@State(Scope.Benchmark)
open class SuspensionsBenchmark : ParametrizedDispatcherBase() {

    companion object {
        const val BENCHMARK_CHAIN_DEPTH = 100
        const val BENCHMARK_FIB_DEPTH = 80
        const val BENCHMARK_SIZE_SUSPENSIONS = 1000
    }

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
        for (i in 1..BENCHMARK_CHAIN_DEPTH) {
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

        check (deepReached == 1) { "deepReached=$deepReached" }
        check (deepCompletion == BENCHMARK_CHAIN_DEPTH) { "deepCompletion=$deepCompletion" }
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
        for (i in 1..BENCHMARK_CHAIN_DEPTH) {
            builder {
                suspendCoroutine { cont ->
                    coroutines.add(cont)
                    COROUTINE_SUSPENDED
                }
                deepDeep(i - 1, BENCHMARK_CHAIN_DEPTH)
            }
        }
        coroutines.last().resume(Unit)
        for (i in 1..BENCHMARK_CHAIN_DEPTH) {
            deepCoroutine!!.resume(Unit)
        }
        coroutines2.forEach { it.resume(Unit) }

        check (deepSuspensionsCount == BENCHMARK_CHAIN_DEPTH) { "deepSuspensionsCount=$deepSuspensionsCount" }
        check (deepReached == 1) { "deepReached=$deepReached" }
        check (deepCompletion == BENCHMARK_CHAIN_DEPTH) { "deepCompletion=$deepCompletion" }
    }


    private suspend fun suspendWithIncrement(value: Int): Int =
        suspendCoroutineUninterceptedOrReturn { x ->
            x.resume(value + 1)
            COROUTINE_SUSPENDED
        }

    @Benchmark
    fun selfResumptions() {

        var result = 0

        builder {
            var acc = 0
            for (i in 1..BENCHMARK_SIZE_SUSPENSIONS) {
                acc = suspendWithIncrement(acc)
            }
            result = acc
        }

        check (result == BENCHMARK_SIZE_SUSPENSIONS) { "result=$result" }
    }

    var resumeCoroutine: (() -> Unit)? = null

    private suspend fun suspendWithIncrement2(value: Int): Int =
        suspendCoroutineUninterceptedOrReturn { x ->
            resumeCoroutine = {
                x.resume(value + 1)
            }
            COROUTINE_SUSPENDED
        }

    @Benchmark
    fun externalResumptions() {
        resumeCoroutine = null
        var result = 0
        var acc = 0

        builder {
            for (i in 1..BENCHMARK_SIZE) {
                acc = suspendWithIncrement2(acc)
            }
            result = acc
        }

        for (i in 1..BENCHMARK_SIZE) {
            resumeCoroutine!!.invoke()
            check (acc == i) { "Failed: expected $i, got $acc" }
        }
        check (result == BENCHMARK_SIZE) { "Failed: expected 50, got $result" }
    }

    private data class Event(val userId: Int, val type: String, val amount: Int)

    private fun rawEvents(n: Int): Sequence<String> = sequence {
        var id = 0
        repeat(n) {
            val userId = (id % 200) + 1
            val type = when (id % 3) { 0 -> "purchase"; 1 -> "refund"; else -> "view" }
            val amount = (id % 97) + 1
            yield("$userId,$type,$amount")
            id++
        }
    }

    private fun parseEvents(raw: Sequence<String>): Sequence<Event> =
        raw.map { line ->
            val parts = line.split(",")
            Event(parts[0].toInt(), parts[1], parts[2].toInt())
        }

    private fun purchasesOnly(events: Sequence<Event>): Sequence<Event> =
        events.filter { it.type == "purchase" }

    private fun enriched(events: Sequence<Event>): Sequence<Pair<Int, Int>> = sequence {
        for (event in events) {
            yield(event.userId to event.amount)
            if (event.amount > 50) yield(event.userId to (event.amount / 2))
        }
    }

    @Benchmark
    fun sequenceMultiplePipelines() {
        val result = enriched(purchasesOnly(parseEvents(rawEvents(BENCHMARK_SIZE))))
            .fold(0L) { acc, (_, amount) -> acc + amount }

        check (result == 222_547L) { "Failed: got $result" }
    }

    @Benchmark
    fun sequenceIterator() {
        val fibonacci = sequence {
            yield(0L)
            yield(1L)
            var a = 0L
            var b = 1L
            while (true) {
                yield(a + b)
                val temp = a
                a = b
                b += temp
            }
        }.iterator()
        var current = 0L
        for (i in 1..BENCHMARK_FIB_DEPTH) {
            current = fibonacci.next()
        }
        check (current == 14_472_334_024_676_221L) { "Failed: expected 6765, got $current" }
    }

    var completionCount = 0

    suspend fun simpleCoroutine(): String {
        return "OK"
    }

    @Benchmark
    fun startCoroutinePerformance() {
        completionCount = 0

        // Start multiple coroutines to measure startCoroutine performance
        repeat(BENCHMARK_SIZE) {
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
        suspendCoroutineUninterceptedOrReturn { continuation ->
            // Immediately resume to measure the suspension/resumption overhead
            continuation.resume("OK")
            COROUTINE_SUSPENDED
        }

    @Benchmark
    fun suspendCoroutineUninterceptedOrReturnPerformance() {
        resumeCount = 0
        var result = "FAIL"

        // Perform multiple suspend/resume cycles
        val coroutine: suspend () -> String = {
            var accumulated = ""
            repeat(BENCHMARK_SIZE_SUSPENSIONS) {
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

        check (resumeCount == BENCHMARK_SIZE_SUSPENSIONS && result == "OK") { "FAIL: $resumeCount" }
    }
}