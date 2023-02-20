import kotlinx.benchmark.gradle.BenchmarksPlugin
import kotlinx.benchmark.gradle.JsBenchmarkTarget
import kotlinx.benchmark.gradle.benchmark
import org.jetbrains.kotlin.gradle.targets.js.d8.D8Exec
import org.jetbrains.kotlin.gradle.targets.js.d8.D8RootPlugin
//import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
//import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin

buildscript {
    repositories {
        gradlePluginPortal()
        maven(uri("./kotlin-compiler"))
    }

    dependencies {
        classpath(files("./kotlinx-benchmarks/kotlinx-benchmark-plugin-0.4.4.jar"))
        classpath("com.squareup:kotlinpoet:1.3.0")
    }
}

plugins {
    kotlin("multiplatform")
}

apply {
    plugin<BenchmarksPlugin>()
}

//with(NodeJsRootPlugin.apply(rootProject)) {
//    nodeVersion = "19.0.0-nightly202206017ad5b420ae"
//    nodeDownloadBaseUrl = "https://nodejs.org/download/nightly/"
//}
//
//with(YarnPlugin.apply(rootProject)) {
//    command = "echo"
//    download = false
//}

//with(D8RootPlugin.apply(rootProject)) {
//    version = "10.7.22"
//}

repositories {
    mavenCentral()
    maven(uri("./kotlin-compiler"))
}

kotlin {
    js(IR) {
        this as org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
        d8()
        //nodejs()
    }
    wasm {
        d8()
        //nodejs()
    }

    wasm("wasmOpt") {
        d8()
        //nodejs()
        applyBinaryen()
    }

    sourceSets.all {
        languageSettings {
            progressiveMode = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-0.4.4.jar"))
            }
        }

        val wasmMain by getting {
            dependencies {
                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-wasm-0.4.4.klib"))
            }
        }

        val wasmOptMain by getting {
            dependencies {
                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-wasm-0.4.4.klib"))
                kotlin.srcDirs("$rootDir/src")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-jsir-0.4.4.klib"))
            }
        }
    }
}

fun reportToTC(csvFile: File, targetName: String) {
    csvFile.readLines().forEachIndexed { i, line ->
        if (i == 0) return@forEachIndexed
        val dataLine = line.split(',')
        val benchmarkName = dataLine[0].replace("\"", "")
        val score = dataLine[4]
        val valueTypeKey = "${targetName}_$benchmarkName"
        println("##teamcity[buildStatisticValue key='$valueTypeKey' value='$score']")
    }
}


val reportAllTargetsToTC = tasks.register("reportAllTargetsToTC")
fun createReportTargetToTC(reportDir: File, targetName: String) {
    val teamcityReport by tasks.register("${targetName}ReportResultsForTC") {
        doLast {
            val fileName = "${targetName}.csv"
            fileTree(reportDir).visit {
                if (file.isFile && file.name == fileName) {
                    reportToTC(file, targetName)
                }
            }
        }
    }
    reportAllTargetsToTC.configure {
        dependsOn(teamcityReport)
    }
}

fun registerReportBundleSizes(bundleSizeList: List<String>) {
    val teamcityReport = tasks.register("reportBundleSizes") {
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
    reportAllTargetsToTC.configure {
        dependsOn(teamcityReport)
    }
}

benchmark {
    configurations {
        with(create("fastMacro")) {
            iterations = 5
            warmups = 5
            iterationTime = 50
            iterationTimeUnit = "millis"
            outputTimeUnit = "millis"
            reportFormat = "csv"
            mode = "avgt"
            advanced("jsUseBridge", true)
            includes.add("macroBenchmarks.MacroBenchmarksFast")
        }
        with(create("slowMacro")) {
            iterations = 1
            warmups = 5
            iterationTime = 0
            iterationTimeUnit = "millis"
            outputTimeUnit = "millis"
            reportFormat = "csv"
            mode = "avgt"
            advanced("jsUseBridge", true)
            includes.add("macroBenchmarks.MacroBenchmarksSlow")
        }
        val slowMicroBenchmarks = listOf(
            "microBenchmarks.StringBenchmark.summarizeSplittedCsv",
            "microBenchmarks.PrimeListBenchmark.calcEratosthenes",
            "microBenchmarks.FibonacciBenchmark.calcSquare",
            "microBenchmarks.superslow.GraphSolverBenchmark.solve",
            "microBenchmarks.LinkedListWithAtomicsBenchmark.ensureNext",
            "microBenchmarks.StringBenchmark.stringConcat",
            "microBenchmarks.StringBenchmark.stringConcatNullable",
            "microBenchmarks.EulerBenchmark.problem4",
            "microBenchmarks.ArrayCopyBenchmark.copyInSameArray",
            "microBenchmarks.BunnymarkBenchmark.testBunnymark",
            "microBenchmarks.CoordinatesSolverBenchmark.solve",
        )
        with(create("fastMicro")) {
            iterations = 5
            warmups = 5
            iterationTime = 50
            iterationTimeUnit = "millis"
            outputTimeUnit = "millis"
            reportFormat = "csv"
            mode = "avgt"
            advanced("jsUseBridge", true)
            includes.add("microBenchmarks")
            excludes.addAll(slowMicroBenchmarks)
        }
        with(create("slowMicro")) {
            iterations = 1
            warmups = 5
            iterationTime = 0
            iterationTimeUnit = "millis"
            outputTimeUnit = "millis"
            reportFormat = "csv"
            mode = "avgt"
            advanced("jsUseBridge", true)
            includes.addAll(slowMicroBenchmarks)
        }
    }

    val reportDir = project.buildDir.resolve(reportsDir)
    targets {
        val bundleSizeList = mutableListOf<String>()
        register("wasm") {
            createReportTargetToTC(reportDir, name)
            bundleSizeList.add(name)
        }
        register("wasmOpt") {
            createReportTargetToTC(reportDir, name)
            bundleSizeList.add(name)
        }
        register("js") {
            (this as JsBenchmarkTarget).jsBenchmarksExecutor = kotlinx.benchmark.gradle.JsBenchmarksExecutor.BuiltIn
            createReportTargetToTC(reportDir, name)
            bundleSizeList.add(name)
        }
        registerReportBundleSizes(bundleSizeList)
    }
}