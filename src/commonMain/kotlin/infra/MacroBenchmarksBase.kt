package macroBenchmarks

open class MacroBenchmarksBase {
    protected fun runBenchmark(macroBenchmark: MacroBenchmark) {
        check(macroBenchmark.innerBenchmarkLoop(macroBenchmark.defaultInnerIterations.max())) {
            "Failed bencmark ${macroBenchmark::class.simpleName}"
        }
    }
}
