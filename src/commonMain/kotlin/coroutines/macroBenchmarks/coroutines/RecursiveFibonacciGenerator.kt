package macroBenchmarks.coroutines

class RecursiveFibonacciGenerator(
    private val n: Int,
) : AbstractGenerator<Int>() {
    private suspend fun runFibonacci(a: Int): Int {
        return when (a) {
            0, 1 -> a.also { yieldValue(it) }
            else -> (runFibonacci(a - 1) + runFibonacci(a - 2)).also { yieldValue(it) }
        }
    }

    override suspend fun generatorBody() {
        runFibonacci(n)
    }
}
