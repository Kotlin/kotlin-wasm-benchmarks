package macroBenchmarks

import kotlinx.benchmark.*
import macroBenchmarks.coroutinesSlowBenchmarks.Coroutines

@State(Scope.Benchmark)
class CoroutineMacroBenchmarks : MacroBenchmarksBase() {
    @Benchmark
    fun coroutineIteration(blackhole: Blackhole) {
        runBenchmark(Coroutines.Iteration(), blackhole)
    }

    @Benchmark
    fun coroutineRecursion(blackhole: Blackhole) {
        runBenchmark(Coroutines.Recursion(), blackhole)
    }
}
