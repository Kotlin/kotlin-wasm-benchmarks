package microBenchmarks

import kotlinx.benchmark.*
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

private class ManualContinuation : Continuation<Unit> {
    override val context: CoroutineContext = EmptyCoroutineContext
    var completed = false
    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow()
        completed = true
    }
}

private fun builder(c: suspend () -> Unit): ManualContinuation {
    val cont = ManualContinuation()
    c.startCoroutine(cont)
    return cont
}

/**
 * Per-invocation state for [CoroutinesIntrinsicsBenchmark.chainSuspensions] and
 * [CoroutinesIntrinsicsBenchmark.deepSuspensions].
 */
private class SuspensionChain(private val deepSuspensionsNum: Int) {
    val starters = ArrayList<Continuation<Unit>>(SUSPENSIONS_AMOUNT)
    val waiters = ArrayList<Continuation<Unit>>(SUSPENSIONS_AMOUNT)
    var deepCoroutine: Continuation<Unit>? = null

    var deepReached = 0
    var deepCompletion = 0
    var deepSuspensionsCount = 0

    fun launchAll() {
        for (i in 1..SUSPENSIONS_AMOUNT) {
            builder {
                suspendCoroutine { cont -> starters.add(cont) }
                deepChain(i - 1)
            }
        }
    }

    private suspend fun deepChain(n: Int) {
        if (n == 0) {
            ++deepReached
            repeat(deepSuspensionsNum) {
                suspendCoroutine<Unit> { cont ->
                    ++deepSuspensionsCount
                    deepCoroutine = cont
                }
            }
        } else {
            suspendCoroutine<Unit> { cont ->
                waiters.add(cont)
                starters[n - 1].resume(Unit)
            }
        }
        ++deepCompletion
    }
}

private suspend fun suspendWithIncrement(value: Int): Int =
    suspendCoroutineUninterceptedOrReturn { x ->
        x.resume(value + 1)
        COROUTINE_SUSPENDED
    }

private class ExternalResumer {
    var resumeCoroutine: (() -> Unit)? = null

    suspend fun suspendWithIncrement(value: Int): Int =
        suspendCoroutineUninterceptedOrReturn { x ->
            resumeCoroutine = {
                x.resume(value + 1)
            }
            COROUTINE_SUSPENDED
        }
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

private suspend fun simpleCoroutine(): String {
    return "OK"
}

private class FrameClock {
    private val pendingFrames = ArrayDeque<Continuation<Long>>()
    private var frameTime = 0L

    val hasPendingFrames: Boolean get() = pendingFrames.isNotEmpty()

    suspend fun awaitFrame(): Long = suspendCoroutine { pendingFrames.add(it) }

    fun dispatchFrame() {
        frameTime += 16 // simulate 16ms frame
        val cont = pendingFrames.removeFirstOrNull() ?: return
        cont.resume(frameTime)
    }
}

private class ThrowingResumer {
    var continuation: Continuation<Int>? = null

    suspend fun suspendAndThrow(): Int = suspendCoroutine { cont ->
        continuation = cont
    }
}

@State(Scope.Benchmark)
open class CoroutinesIntrinsicsBenchmark {

    private lateinit var resumeException: RuntimeException

    @Setup
    fun setup() {
        resumeException = RuntimeException("x")
    }

    @Benchmark
    fun chainSuspensions(blackhole: Blackhole) {
        val chain = SuspensionChain(deepSuspensionsNum = 0)
        chain.launchAll()
        chain.starters.last().resume(Unit)
        chain.waiters.forEach { it.resume(Unit) }

        check (chain.deepReached == 1) { "deepReached=${chain.deepReached}" }
        check (chain.deepCompletion == SUSPENSIONS_AMOUNT) { "deepCompletion=${chain.deepCompletion}" }
        val result = chain.deepReached + chain.deepCompletion
        blackhole.consume(result)
    }

    @Benchmark
    fun deepSuspensions(blackhole: Blackhole) {
        val chain = SuspensionChain(deepSuspensionsNum = SUSPENSIONS_AMOUNT)
        chain.launchAll()
        chain.starters.last().resume(Unit)
        repeat (SUSPENSIONS_AMOUNT) {
            chain.deepCoroutine!!.resume(Unit)
        }
        chain.waiters.forEach { it.resume(Unit) }

        check (chain.deepSuspensionsCount == SUSPENSIONS_AMOUNT) { "deepSuspensionsCount=${chain.deepSuspensionsCount}" }
        check (chain.deepReached == 1) { "deepReached=${chain.deepReached}" }
        check (chain.deepCompletion == SUSPENSIONS_AMOUNT) { "deepCompletion=${chain.deepCompletion}" }
        val result = chain.deepSuspensionsCount + chain.deepReached + chain.deepCompletion
        blackhole.consume(result)
    }

    @Benchmark
    fun selfResumptions(blackhole: Blackhole) {
        var result = 0

        builder {
            var acc = 0
            repeat (SUSPENSIONS_AMOUNT) {
                acc = suspendWithIncrement(acc)
            }
            result = acc
        }

        check (result == SUSPENSIONS_AMOUNT) { "result=$result" }
        blackhole.consume(result)
    }

    @Benchmark
    fun externalResumptions(blackhole: Blackhole) {
        val resumer = ExternalResumer()
        var result = 0

        builder {
            var acc = 0
            repeat (BENCHMARK_SIZE) {
                acc = resumer.suspendWithIncrement(acc)
            }
            result = acc
        }

        repeat(BENCHMARK_SIZE) {
            resumer.resumeCoroutine!!.invoke()
        }
        check (result == BENCHMARK_SIZE) { "Failed: expected $BENCHMARK_SIZE, got $result" }
        blackhole.consume(result)
    }

    @Benchmark
    fun sequenceMultiplePipelines(blackhole: Blackhole) {
        val result = enriched(purchasesOnly(parseEvents(rawEvents(BENCHMARK_SIZE))))
            .fold(0L) { acc, (_, amount) -> acc + amount }

        check (result == 222_547L) { "Failed: got $result" }
        blackhole.consume(result)
    }

    @Benchmark
    fun sequenceIterator(blackhole: Blackhole) {
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
        check (current == 137) { "Failed: expected 137, got $current" }
        blackhole.consume(current)
    }

    @Benchmark
    fun startCoroutinePerformance(blackhole: Blackhole) {
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
        blackhole.consume(completionCount)
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
        blackhole.consume(completionCount)
    }

    @Benchmark
    fun createCoroutinePerformance(blackhole: Blackhole) {
        // Create multiple coroutines to measure createCoroutine performance
        repeat(BENCHMARK_SIZE) {
            val suspendFun: suspend () -> String = ::simpleCoroutine

            val coroutine = suspendFun.createCoroutine(Continuation(EmptyCoroutineContext) {})
            blackhole.consume(coroutine)
        }
    }

    @Benchmark
    fun createAndResumeCoroutinePerformance(blackhole: Blackhole) {
        var completionCount = 0

        val coroutines = List(BENCHMARK_SIZE) {
            val suspendFun: suspend () -> String = ::simpleCoroutine

            suspendFun.createCoroutine(Continuation(EmptyCoroutineContext) { result ->
                if (result.isSuccess && result.getOrNull() == "OK") {
                    completionCount++
                }
            })
        }

        coroutines.forEach { it.resume(Unit) }
        check (completionCount == BENCHMARK_SIZE) { "Failed: expected $BENCHMARK_SIZE to complete, $completionCount completed." }
        blackhole.consume(completionCount)
    }

    @Benchmark
    fun createCoroutineUninterceptedPerformance(blackhole: Blackhole) {
        // Create multiple coroutines to measure createCoroutineUnintercepted performance
        repeat(BENCHMARK_SIZE) {
            val suspendFun: suspend () -> String = ::simpleCoroutine

            val coroutine = suspendFun.createCoroutineUnintercepted(Continuation(EmptyCoroutineContext) {})
            blackhole.consume(coroutine)
        }
    }

    // Benchmark emulating compose lazy viewers
    @Benchmark
    fun frameClockSuspensions(blackhole: Blackhole) {
        val clock = FrameClock()
        var consumed = 0L

        builder {
            repeat(BENCHMARK_SIZE) {
                val time = clock.awaitFrame()  // suspends, resumed by dispatchFrame
                consumed += time               // do "work" per frame
            }
        }

        repeat(BENCHMARK_SIZE) { clock.dispatchFrame() }

        check(!clock.hasPendingFrames)
        // 16 * (1 + 2 + ... + BENCHMARK_SIZE)
        check(consumed == 8L * BENCHMARK_SIZE * (BENCHMARK_SIZE + 1)) { "Failed: got $consumed" }
        blackhole.consume(consumed)
    }

    @Benchmark
    fun externalResumptionsWithException(blackhole: Blackhole) {
        val exception = resumeException
        val resumer = ThrowingResumer()
        var caught = 0

        builder {
            repeat(BENCHMARK_SIZE) {
                try {
                    resumer.suspendAndThrow()
                } catch (_: Exception) {
                    caught++
                }
            }
        }

        repeat(BENCHMARK_SIZE) {
            resumer.continuation!!.resumeWithException(exception)
        }

        check(caught == BENCHMARK_SIZE)
        blackhole.consume(caught)
    }
}
