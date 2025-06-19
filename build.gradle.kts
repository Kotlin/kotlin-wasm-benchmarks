@file:OptIn(KotlinxBenchmarkPluginInternalApi::class, ExperimentalWasmDsl::class)

import kotlinx.benchmark.gradle.*
import de.undercouch.gradle.tasks.download.Download
import kotlinx.benchmark.gradle.internal.KotlinxBenchmarkPluginInternalApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmSubTargetContainerDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.d8.D8Plugin
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.d8.D8EnvSpec
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.js.ir.*

buildscript {
    repositories {
        gradlePluginPortal()
        maven(uri("./kotlin-compiler"))
    }

    val kotlin_version: String by project

    dependencies {
        classpath(files("./kotlinx-benchmarks/kotlinx-benchmark-plugin-0.5.0.jar"))
        classpath("com.squareup:kotlinpoet:1.3.0")
        classpath("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlin_version")
    }
}

plugins {
    kotlin("multiplatform")
    id("de.undercouch.download") version "5.5.0"
}

apply {
    plugin<BenchmarksPlugin>()
}

apply<NodeJsPlugin>()
the<NodeJsEnvSpec>().apply {
    version.set("23.6.0")
}

apply<BinaryenRootPlugin>()
the<BinaryenRootEnvSpec>().version.set("123")

apply<D8Plugin>()
the<D8EnvSpec>().version.set("13.4.61")

allprojects.forEach {
    it.tasks.withType<org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask>().configureEach {
        args.add("--ignore-engines")
    }
}

repositories {
    mavenCentral()
    maven(uri("./kotlin-compiler"))
    maven(uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/"))
}

kotlin {
    js {
        this as KotlinJsIrTarget
        //d8()
        nodejs()
    }

    wasmJs("wasm") {
        d8()
        //nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-0.5.0.jar"))
            }
        }

        val wasmMain by getting {
            dependencies {
                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-wasm-js-0.5.0.klib"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-js-0.5.0.klib"))
            }
        }
    }
}

val reportAllTargetsToTC = tasks.register("reportAllTargetsToTC")

benchmark {
    configurations {
        with(create("fastMacro")) {
            iterations = 5
            warmups = 5
            iterationTime = 50
            iterationTimeUnit = "millis"
            outputTimeUnit = "millis"
            reportFormat = "json"
            mode = "avgt"
            advanced("jsUseBridge", true)
            includes.add("macroBenchmarks.MacroBenchmarksFast")
        }
        with(create("slowMacro")) {
            iterations = 1
            warmups = 5
            iterationTime = 1
            iterationTimeUnit = "nanos"
            outputTimeUnit = "millis"
            reportFormat = "json"
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
            reportFormat = "json"
            mode = "avgt"
            advanced("jsUseBridge", true)
            includes.add("microBenchmarks")
            excludes.addAll(slowMicroBenchmarks)
        }
        with(create("slowMicro")) {
            iterations = 1
            warmups = 5
            iterationTime = 1
            iterationTimeUnit = "nanos"
            outputTimeUnit = "millis"
            reportFormat = "json"
            mode = "avgt"
            advanced("jsUseBridge", true)
            includes.addAll(slowMicroBenchmarks)
        }
    }

    val reportDir = project.buildDir.resolve(reportsDir)
    val compileSyncDir = project.buildDir.resolve("compileSync")
    targets {
        val bundleSizeList = mutableListOf<Pair<String, File>>()
        val reportTasks = mutableListOf<TaskProvider<Task>>()
        register("wasm") {
            reportTasks.add(createReportTargetToTC(reportDir, name))
            reportTasks.add(createReportTargetToTC(reportDir, "jsShell_$name"))

            val wasmCompileSyncDir = compileSyncDir
                .resolve("wasm")
                .resolve("wasmBenchmark")
                .resolve("wasmBenchmarkDevelopmentExecutable")
                .resolve("kotlin")
            bundleSizeList.add("wasm" to wasmCompileSyncDir)
        }
        register("wasmOpt") {
            reportTasks.add(createReportTargetToTC(reportDir, name))
            reportTasks.add(createReportTargetToTC(reportDir, "jsShell_$name"))
            val wasmCompileSyncDir = compileSyncDir
                .resolve("wasm")
                .resolve("wasmBenchmark")
                .resolve("wasmBenchmarkProductionExecutable")
                .resolve("optimized")
            bundleSizeList.add("wasmOpt" to wasmCompileSyncDir)
        }
        register("js") {
            (this as JsBenchmarkTarget).jsBenchmarksExecutor = JsBenchmarksExecutor.BuiltIn
            reportTasks.add(createReportTargetToTC(reportDir, name))
            reportTasks.add(createReportTargetToTC(reportDir, "jsShell_$name"))
            val wasmCompileSyncDir = compileSyncDir
                .resolve("js")
                .resolve("jsBenchmark")
            bundleSizeList.add("js" to wasmCompileSyncDir)
        }

        reportTasks.add(registerReportBundleSizes(bundleSizeList))

        reportAllTargetsToTC.configure {
            reportTasks.forEach {
                dependsOn(it)
            }
        }
    }
}


/////////////////////////

val jsShellDirectory = "https://archive.mozilla.org/pub/firefox/releases/134.0.2/jsshell"
val jsShellSuffix = when (currentOsType) {
    OsType(OsName.LINUX, OsArch.X86_32) -> "linux-i686"
    OsType(OsName.LINUX, OsArch.X86_64) -> "linux-x86_64"
    OsType(OsName.MAC, OsArch.X86_64),
    OsType(OsName.MAC, OsArch.ARM64) -> "mac"
    OsType(OsName.WINDOWS, OsArch.X86_32) -> "win32"
    OsType(OsName.WINDOWS, OsArch.X86_64) -> "win64"
    else -> error("unsupported os type $currentOsType")
}
val jsShellLocation = "$jsShellDirectory/jsshell-$jsShellSuffix.zip"

val downloadedTools = File(buildDir, "tools")

val downloadJsShell = tasks.register("jsShellDownload", Download::class) {
    src(jsShellLocation)
    dest(File(downloadedTools, "jsshell-$jsShellSuffix.zip"))
    overwrite(false)
}

val unzipJsShell = tasks.register("jsShellUnzip", Copy::class) {
    dependsOn(downloadJsShell)
    from(zipTree(downloadJsShell.get().dest))
    val unpackedDir = File(downloadedTools, "jsshell-$jsShellSuffix")
    into(unpackedDir)
}

fun Project.getExecutableFile(compilation: KotlinJsIrCompilation, mode: KotlinJsBinaryMode): Provider<RegularFile> {
    val kotlinTarget = compilation.target as KotlinJsIrTarget
    val binary = kotlinTarget.binaries.executable(compilation)
        .first { it.mode == mode } as JsIrBinary
    val extension = if (kotlinTarget.platformType == KotlinPlatformType.wasm) "mjs" else "js"
    val outputFileName = binary.linkTask.flatMap { task ->
        task.compilerOptions.moduleName.map { "$it.$extension" }
    }
    val destinationDir = binary.linkSyncTask.flatMap { it.destinationDirectory }
    val executableFile = destinationDir.zip(outputFileName) { dir, fileName -> dir.resolve(fileName) }
    return project.layout.file(executableFile)
}

fun Project.createJsShellExec(
    config: BenchmarkConfiguration,
    target: BenchmarkTarget,
    compilation: KotlinJsIrCompilation,
    taskName: String,
    mode: KotlinJsBinaryMode,
    fileNamePostfix: String,
): TaskProvider<Exec> = tasks.register(taskName, Exec::class) {
    dependsOn(compilation.runtimeDependencyFiles)
    dependsOn(unzipJsShell)

    group = BenchmarksPlugin.BENCHMARKS_TASK_GROUP
    description = "Executes benchmark for '${target.name}' with jsShell"

    val newArgs = mutableListOf<String>()
    executable = File(unzipJsShell.get().destinationDir, "js").absolutePath

    val productionBinary = getExecutableFile(compilation, mode) ?: error("Not found production binary")
    dependsOn(productionBinary)

    val inputFileAsFile = productionBinary.get().asFile
    workingDir = inputFileAsFile.parentFile
    if (compilation.target.platformType == KotlinPlatformType.wasm) {
        newArgs.add("--module=${inputFileAsFile.absolutePath}")
    } else {
        newArgs.add("--file=${inputFileAsFile.absolutePath}")
    }
    val reportFile = setupReporting(target, config, "jsShell_", fileNamePostfix)
    newArgs.add("--")
    newArgs.add(writeParameters(target.name, reportFile, traceFormat(), config).absolutePath)
    args = newArgs
    standardOutput = ConsoleAndFilesOutputStream()
}


fun Project.createJsEngineBenchmarkExecTask(
    config: BenchmarkConfiguration,
    target: BenchmarkTarget,
    compilation: KotlinJsIrCompilation,
    mode: KotlinJsBinaryMode,
) {
    val postfix = if (mode == KotlinJsBinaryMode.DEVELOPMENT) "" else "Opt"
    val taskName = "jsShell_${target.name}$postfix${config.capitalizedName()}${BenchmarksPlugin.BENCHMARK_EXEC_SUFFIX}"
    val compilationTarget = compilation.target
    if (compilationTarget is KotlinWasmSubTargetContainerDsl) {
        val execTask = createJsShellExec(config, target, compilation, taskName, mode, postfix)
        tasks.getByName(config.prefixName(BenchmarksPlugin.RUN_BENCHMARKS_TASKNAME)).dependsOn(execTask)
    }
}

afterEvaluate {
    val extension = extensions.getByName(BenchmarksPlugin.BENCHMARK_EXTENSION_NAME) as BenchmarksExtension
    extension.targets.forEach { target ->
        val compilation = when (target) {
            is WasmBenchmarkTarget -> target.compilation
            is JsBenchmarkTarget -> target.compilation
            else -> null
        }
        if (compilation != null) {
            target.extension.configurations.forEach { config ->
                val benchmarkCompilation = compilation.target.compilations.maybeCreate(target.name + BenchmarksPlugin.BENCHMARK_COMPILATION_SUFFIX) as KotlinJsIrCompilation
                createJsEngineBenchmarkExecTask(config, target, benchmarkCompilation, KotlinJsBinaryMode.PRODUCTION)
                createJsEngineBenchmarkExecTask(config, target, benchmarkCompilation, KotlinJsBinaryMode.DEVELOPMENT)
            }
        }
    }
}

tasks.withType<KotlinJsCompile> {
    compilerOptions.freeCompilerArgs.add("-Xskip-prerelease-check")
    compilerOptions.freeCompilerArgs.add("-Xwasm-enable-array-range-checks")
}

rootProject.the<YarnRootExtension>().yarnLockMismatchReport =
    org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport.NONE