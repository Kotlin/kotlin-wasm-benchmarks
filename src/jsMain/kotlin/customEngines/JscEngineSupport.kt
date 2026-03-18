@file:OptIn(KotlinxBenchmarkRuntimeInternalApi::class)

package customEngines

import kotlinx.benchmark.Measurer
import kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi
import kotlin.time.Duration.Companion.seconds

private fun jscArguments(): String =
    js("typeof globalThis.arguments !== 'undefined' ? globalThis.arguments.join(' ') : \"\"")

internal object JscEngineSupport : StandaloneJsVmSupport() {
    override fun writeFile(path: String, content: String) {
        print("<FILE:$path>$content<ENDFILE>")
    }

    override fun arguments(): Array<out String> =
        jscArguments().split(' ').toTypedArray()

    override fun isSupported(): Boolean = isJscEngine()

    override fun getMeasurer(): Measurer = JscMeasurer()

    fun initializeConsoleLog(): Unit = doInitializeConsoleLog()
}

private fun isJscEngine(): Boolean = js("typeof(jscOptions) !== 'undefined'")

private fun doInitializeConsoleLog(): Unit = js("{ console = { log: print } }")

private fun toFixed9(value: dynamic): Double =
    js("value.toFixed(9)")

private fun getPreciseTime(): dynamic =
    js("preciseTime()")

private class JscMeasurer : Measurer() {
    private var start: dynamic = 0.0
    override fun measureStart() {
        start = getPreciseTime()
    }

    override fun measureFinish(): Long {
        val end = getPreciseTime()
        val startInNs = toFixed9(start!!).seconds.inWholeNanoseconds
        val endInNs = toFixed9(end).seconds.inWholeNanoseconds
        return endInNs - startInNs
    }
}