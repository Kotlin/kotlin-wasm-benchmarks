package macroBenchmarks

import kotlinx.benchmark.Blackhole

open class MacroBenchmarksBase {
    protected fun runBenchmark(macroBenchmark: MacroBenchmark, blackhole: Blackhole) {
        check(macroBenchmark.innerBenchmarkLoop(macroBenchmark.defaultInnerIterations.max(), blackhole)) {
            "Failed benchmark ${macroBenchmark::class.simpleName}"
        }
    }
}
