@file:OptIn(ExperimentalWasmJsInterop::class, KotlinxBenchmarkRuntimeExperimentalApi::class)

package customEngines

import kotlinx.benchmark.BenchmarkEngineSupport
import kotlinx.benchmark.KotlinxBenchmarkRuntimeExperimentalApi
import kotlinx.benchmark.Measurer
import kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi
import kotlin.time.Duration.Companion.milliseconds

private fun browserEngineReadFile(path: String): String =
    js("globalThis.read(path)")

@OptIn(KotlinxBenchmarkRuntimeInternalApi::class)
internal abstract class StandaloneJsVmSupport : BenchmarkEngineSupport() {
    override fun writeFile(path: String, content: String) =
        println("<FILE:$path>$content<ENDFILE>")

    override fun readFile(path: String): String =
        browserEngineReadFile(path)

    override fun getMeasurer(): Measurer = StandaloneJsVmMeasurer()
}

private fun getPerformance(): JsAny =
    js("(typeof self !== 'undefined' ? self : globalThis).performance")

private fun performanceNow(performance: JsAny): Double =
    js("performance.now()")

@OptIn(KotlinxBenchmarkRuntimeInternalApi::class)
private class StandaloneJsVmMeasurer : Measurer() {
    private val performance: JsAny = getPerformance()
    private var start: Double = 0.0
    override fun measureStart() {
        start = performanceNow(performance)
    }

    override fun measureFinish(): Long {
        val end = performanceNow(performance)
        val startInNs = start.milliseconds.inWholeNanoseconds
        val endInNs = end.milliseconds.inWholeNanoseconds
        return endInNs - startInNs
    }
}