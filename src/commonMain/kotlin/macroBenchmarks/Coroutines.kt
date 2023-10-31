package macroBenchmarks

import macroBenchmarks.coroutines.AbstractGenerator
import macroBenchmarks.coroutines.ClosedRangeGenerator
import macroBenchmarks.coroutines.CoroutineStub
import macroBenchmarks.coroutines.RecursiveFibonacciGenerator

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
