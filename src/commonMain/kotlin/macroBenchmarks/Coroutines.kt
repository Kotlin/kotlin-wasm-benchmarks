package macroBenchmarks

import macroBenchmarks.coroutines.AbstractGenerator
import macroBenchmarks.coroutines.ClosedRangeGenerator
import macroBenchmarks.coroutines.CoroutineStub
import macroBenchmarks.coroutines.RecursiveFibonacciGenerator

/**
 * This case benchmarks Kotlin suspend function overhead in coroutines.
 * There are two cases:
 *   [Iteration] - benchmarks simple suspend function calls when the suspend call stack has only one call.
 *   [Recursion] - benchmarks suspend functions that call other suspend functions.
 *      The suspend call stack has many calls. The deep call stack is emulated with recursion.
 */
sealed class Coroutines: MacroBenchmark() {
    protected abstract fun makeGenerator(): AbstractGenerator<Int>
    protected abstract val expectedSum: Int

    override fun benchmark(): Any {
        var sum = 0

        val generator = makeGenerator()
        generator.resetGenerator()
        CoroutineStub.evaluate {
            while (generator.hasNext()) {
                sum += generator.nextValue()
            }
        }

        return sum
    }

    override fun verifyResult(result: Any) = (result as? Int) == expectedSum

    class Iteration: Coroutines() {
        override fun makeGenerator() = ClosedRangeGenerator(-200000, 200001, 1)
        override val expectedSum: Int = 200001
    }

    class Recursion: Coroutines() {
        override fun makeGenerator() = RecursiveFibonacciGenerator(30)
        override val expectedSum: Int = 18394910
    }
}
