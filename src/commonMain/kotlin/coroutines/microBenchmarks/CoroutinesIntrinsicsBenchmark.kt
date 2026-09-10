package microBenchmarks

import kotlinx.benchmark.*
import macroBenchmarks.coroutines.ParametrizedDispatcherBase
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.intrinsics.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

// Bigger values will result in `Maximum call stack size exceeded` for State Machine.
private const val SUSPENSIONS_AMOUNT = 500

// Bigger values lead to Fatal process out of memory: StackMemory::StackSegment::StackSegment with Stack Switching on TC.
private const val CREATE_SIZE = 1_000

@State(Scope.Benchmark)
open class CoroutinesIntrinsicsBenchmark : ParametrizedDispatcherBase() {

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
        for (i in 1..SUSPENSIONS_AMOUNT) {
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
        coroutines.clear()
        coroutines2.clear()

        check (deepReached == 1) { "deepReached=$deepReached" }
        check (deepCompletion == SUSPENSIONS_AMOUNT) { "deepCompletion=$deepCompletion" }
    }

    suspend fun deepDeep(n: Int, deepSuspensionsNum: Int) {
        if (n == 0) {
            ++deepReached
            repeat (deepSuspensionsNum) {
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
        for (i in 1..SUSPENSIONS_AMOUNT) {
            builder {
                suspendCoroutine { cont ->
                    coroutines.add(cont)
                    COROUTINE_SUSPENDED
                }
                deepDeep(i - 1, SUSPENSIONS_AMOUNT)
            }
        }
        coroutines.last().resume(Unit)
        repeat (SUSPENSIONS_AMOUNT) {
            deepCoroutine!!.resume(Unit)
        }
        coroutines2.forEach { it.resume(Unit) }
        coroutines.clear()
        coroutines2.clear()
        deepCoroutine = null

        check (deepSuspensionsCount == SUSPENSIONS_AMOUNT) { "deepSuspensionsCount=$deepSuspensionsCount" }
        check (deepReached == 1) { "deepReached=$deepReached" }
        check (deepCompletion == SUSPENSIONS_AMOUNT) { "deepCompletion=$deepCompletion" }
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
            repeat (SUSPENSIONS_AMOUNT) {
                acc = suspendWithIncrement(acc)
            }
            result = acc
        }

        check (result == SUSPENSIONS_AMOUNT) { "result=$result" }
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
            repeat (BENCHMARK_SIZE) {
                acc = suspendWithIncrement2(acc)
            }
            result = acc
        }

        for (i in 1..BENCHMARK_SIZE) {
            resumeCoroutine!!.invoke()
            check (acc == i) { "Failed: expected $i, got $acc" }
        }
        resumeCoroutine = null
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
        val collatzIterator = sequence {
            var n = 1
            while (true) {
                var x = n++
                while (x != 1) {
                    yield(x)
                    x = if (x % 2 == 0) x / 2 else 3 * x + 1
                }
            }
        }.iterator()
        var current = 0
        repeat (BENCHMARK_SIZE) {
            current = collatzIterator.next()
        }
        check (current == 137) { "Failed: expected 6765, got $current" }
    }

    suspend fun simpleCoroutine(): String {
        return "OK"
    }

    @Benchmark
    fun startCoroutinePerformance() {
        var completionCount = 0

        // Start multiple coroutines to measure startCoroutine performance
        repeat(BENCHMARK_SIZE) {
            val suspendFun: suspend () -> String = ::simpleCoroutine

            suspendFun.startCoroutine(Continuation(EmptyCoroutineContext) { result ->
                if (result.isSuccess && result.getOrNull() == "OK") {
                    completionCount++
                }
            })
        }
        check (completionCount == BENCHMARK_SIZE) { "Failed: expected $BENCHMARK_SIZE to complete, $completionCount completed." }
    }

    @Benchmark
    fun startCoroutineUninterceptedOrReturnPerformance(blackhole: Blackhole) {
        var completionCount = 0

        // Start multiple coroutines to measure startCoroutine performance
        repeat(BENCHMARK_SIZE) {
            val suspendFun: suspend () -> String = ::simpleCoroutine

            val result = suspendFun.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) {})
            if (result == "OK") {
                completionCount++
            }
        }
        check (completionCount == BENCHMARK_SIZE) { "Failed: expected $BENCHMARK_SIZE to complete, $completionCount completed." }
    }


//    @Benchmark
//    fun createCoroutinePerformance(blackhole: Blackhole) {
//        // Create multiple coroutines to measure createCoroutine performance
//
//        repeat(CREATE_SIZE) {
//            val suspendFun: suspend () -> String = ::simpleCoroutine
//
//            val coroutine = suspendFun.createCoroutine(Continuation(EmptyCoroutineContext) {})
//            blackhole.consume(coroutine)
//        }
//    }

//    @Benchmark
//    fun createAndResumeCoroutinePerformance() {
//
//        var completionCount = 0
//
//        val coroutines = List(CREATE_SIZE) {
//            val suspendFun: suspend () -> String = ::simpleCoroutine
//
//            val coroutine = suspendFun.createCoroutine(Continuation(EmptyCoroutineContext) { result ->
//                if (result.isSuccess && result.getOrNull() == "OK") {
//                    completionCount++
//                }
//            })
//            coroutine
//        }
//
//        coroutines.forEach { it.resume(Unit) }
//        check (completionCount == CREATE_SIZE) { "Failed: expected $BENCHMARK_SIZE to complete, $completionCount completed." }
//    }

//    @Benchmark
//    fun createCoroutineUninterceptedPerformance(blackhole: Blackhole) {
//
//        // Create multiple coroutines to measure createCoroutineUnintercepted performance
//        repeat(CREATE_SIZE) {
//            val suspendFun: suspend () -> String = ::simpleCoroutine
//
//            val coroutine = suspendFun.createCoroutineUnintercepted(Continuation(EmptyCoroutineContext) {})
//            blackhole.consume(coroutine)
//        }
//    }

    val pendingFrames = ArrayDeque<Continuation<Long>>()
    var frameTime = 0L

    suspend fun awaitFrame(): Long = suspendCoroutine { pendingFrames.add(it) }

    fun dispatchFrame() {
        frameTime += 16 // simulate 16ms frame
        val cont = pendingFrames.removeFirstOrNull() ?: return
        cont.resume(frameTime)
    }

    // benchmark emulating compose lazy viewers
    @Benchmark
    fun frameClockSuspensions() {
        var consumed = 0L

        builder {
            repeat(BENCHMARK_SIZE) {
                val time = awaitFrame()  // suspends, resumed by dispatchFrame
                consumed += time         // do "work" per frame
            }
        }

        repeat(BENCHMARK_SIZE) { dispatchFrame() }

        check(pendingFrames.isEmpty())
    }

    var throwContinuation: Continuation<Int>? = null

    private suspend fun suspendAndThrow(): Int = suspendCoroutine { cont ->
        throwContinuation = cont
    }

    @Benchmark
    fun externalResumptionsWithException() {
        var caught = 0

        builder {
            repeat(BENCHMARK_SIZE) {
                try {
                    suspendAndThrow()
                } catch (e: Exception) {
                    caught++
                }
            }
        }

        repeat(BENCHMARK_SIZE) {
            throwContinuation!!.resumeWithException(RuntimeException("x"))
        }

        check(caught == BENCHMARK_SIZE)
    }
}