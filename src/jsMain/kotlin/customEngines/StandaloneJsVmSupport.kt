@file:OptIn(KotlinxBenchmarkRuntimeInternalApi::class)

package customEngines

import kotlinx.benchmark.BenchmarkEngineSupport
import kotlinx.benchmark.Measurer
import kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi
import kotlin.time.Duration.Companion.milliseconds

private external fun read(path: String): String

internal abstract class StandaloneJsVmSupport : BenchmarkEngineSupport() {
    override fun readFile(path: String): String =
        read(path)

    override fun getMeasurer(): Measurer = StandaloneJsVmMeasurer()
}

private class StandaloneJsVmMeasurer : Measurer() {
    private val performance: dynamic = js("(typeof self !== 'undefined' ? self : globalThis).performance")
    private var start: dynamic = 0.0
    override fun measureStart() {
        start = performance.now()
    }

    override fun measureFinish(): Long {
        val end = performance.now()
        val startInNs = (start as Double).milliseconds.inWholeNanoseconds
        val endInNs = (end as Double).milliseconds.inWholeNanoseconds
        return endInNs - startInNs
    }
}