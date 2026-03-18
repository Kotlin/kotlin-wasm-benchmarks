@file:OptIn(KotlinxBenchmarkRuntimeExperimentalApi::class)

package customEngines

import kotlinx.benchmark.KotlinxBenchmarkRuntimeExperimentalApi
import kotlinx.benchmark.internal.KotlinxBenchmarkRuntimeInternalApi
import kotlinx.benchmark.overrideEngineSupport

@Suppress("DEPRECATION")
@OptIn(ExperimentalStdlibApi::class, KotlinxBenchmarkRuntimeInternalApi::class)
@EagerInitialization
private val initializeCustomEngines: Unit = run {
    val currentEngine = listOf(D8EngineSupport, JscEngineSupport, SpiderMonkeyEngineSupport).firstOrNull { it.isSupported() }
    if (currentEngine != null) {
        if (currentEngine is JscEngineSupport) {
            currentEngine.initializeConsoleLog()
        }
        overrideEngineSupport(currentEngine)
    }
}