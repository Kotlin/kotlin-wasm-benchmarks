package macroBenchmarks

import kotlinx.benchmark.*
import macroBenchmarks.coroutinesSlowBenchmarks.Coroutines

@State(Scope.Benchmark)
class CoroutineMacroBenchmarks : MacroBenchmarksBase() {
    @Benchmark
    fun coroutineIteration() {
        runBenchmark(Coroutines.Iteration())
    }

    @Benchmark
    fun coroutineRecursion() {
        runBenchmark(Coroutines.Recursion())
    }
}
