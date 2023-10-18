import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File

@Serializable
private data class BenchmarkReport(
    val benchmark: String,
    val primaryMetric: PrimaryMetric
)

@Serializable
private data class PrimaryMetric(
    val score: Double
)

private val serializer = Json { ignoreUnknownKeys = true }

private fun reportToTC(jsonFile: File, targetName: String) {
    val jsonStr = jsonFile.readText()

    val report = serializer.decodeFromString<List<BenchmarkReport>>(jsonStr)

    for (benchmark in report) {
        val valueTypeKey = "${targetName}_${benchmark.benchmark}"
        val score = benchmark.primaryMetric.score
        println("##teamcity[buildStatisticValue key='$valueTypeKey' value='$score']")
    }
}

fun Project.registerReportBundleSizes(bundleSizeList: List<String>): TaskProvider<Task> = tasks.register("reportBundleSizes") {
    doLast {
        for (target in bundleSizeList) {
            val compileSyncDir = project.buildDir.resolve("compileSync").resolve(target)
            val score = compileSyncDir.walk().sumOf {
                val isBundleFile = it.extension.let { ext -> ext == "js" || ext == "wasm" || ext == "mjs" }
                if (isBundleFile) it.length() else 0
            }
            val valueTypeKey = "${target}_bundleSize"
            println("##teamcity[buildStatisticValue key='$valueTypeKey' value='$score']")
        }
    }
}

fun Project.createReportTargetToTC(reportDir: File, targetName: String): TaskProvider<Task> = tasks.register("${targetName}ReportResultsForTC") {
    doLast {
        val fileName = "${targetName}.json"
        fileTree(reportDir).visit {
            if (file.isFile && file.name == fileName) {
                reportToTC(file, targetName)
            }
        }
    }
}
