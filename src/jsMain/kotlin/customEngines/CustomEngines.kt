
package customEngines

import kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi
import kotlinx.benchmark.overrideEngineSupport

@ExperimentalJsExport
@Suppress("DEPRECATION")
@OptIn(ExperimentalStdlibApi::class, KotlinxBenchmarkRuntimeInternalApi::class)
@EagerInitialization
@JsExport
val initializeCustomEngines: Int = run {
    val currentEngine = listOf(D8EngineSupport, JscEngineSupport, SpiderMonkeyEngineSupport).firstOrNull { it.isSupported() }
    if (currentEngine != null) {
        if (currentEngine is JscEngineSupport) {
            currentEngine.initializeConsoleLog()
        }
        overrideEngineSupport(currentEngine)
    }
    42
}