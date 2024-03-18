import kotlinx.benchmark.gradle.*
import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmSubTargetContainerDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.d8.D8RootPlugin
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget

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

with(NodeJsRootPlugin.apply(rootProject)) {
    nodeVersion = "21.0.0-v8-canary202309167e82ab1fa2"
    nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
}

with(BinaryenRootPlugin.apply(rootProject)) {
    version = "116"
}

with(D8RootPlugin.apply(rootProject)) {
    version = "11.9.125"
}

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

val distinguishAttribute = Attribute.of("kotlinx-benchmark-distinguishAttribute", String::class.java)

kotlin {
    js(IR) {
        this as KotlinJsIrTarget
        //d8()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs("wasm") {
        d8()
        attributes.attribute(distinguishAttribute, "wasm")
        //nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs("wasmOpt") {
        d8()
        //nodejs()
        applyBinaryen()
        attributes.attribute(distinguishAttribute, "wasmOpt")
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

        val wasmOptMain by getting {
            dependencies {
                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-wasm-js-0.5.0.klib"))
                kotlin.srcDirs("$rootDir/src/wasmMain")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-jsir-0.5.0.klib"))
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
    targets {
        val bundleSizeList = mutableListOf<String>()
        val reportTasks = mutableListOf<TaskProvider<Task>>()
        register("wasm") {
            reportTasks.add(createReportTargetToTC(reportDir, name))
            bundleSizeList.add(name)
            reportTasks.add(createReportTargetToTC(reportDir, "jsShell_$name"))
            bundleSizeList.add("jsShell_$name")
        }
        register("wasmOpt") {
            reportTasks.add(createReportTargetToTC(reportDir, name))
            bundleSizeList.add(name)
            reportTasks.add(createReportTargetToTC(reportDir, "jsShell_$name"))
            bundleSizeList.add("jsShell_$name")
        }
        register("js") {
            (this as JsBenchmarkTarget).jsBenchmarksExecutor = JsBenchmarksExecutor.BuiltIn
            reportTasks.add(createReportTargetToTC(reportDir, name))
            bundleSizeList.add(name)
            reportTasks.add(createReportTargetToTC(reportDir, "jsShell_$name"))
            bundleSizeList.add("jsShell_$name")
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

val jsShellDirectory = "https://archive.mozilla.org/pub/firefox/nightly/2023/09/2023-09-21-09-13-27-mozilla-central"
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

fun tryGetBinary(compilation: KotlinJsCompilation, mode: KotlinJsBinaryMode): JsIrBinary? =
    (compilation.target as? KotlinJsIrTarget)
        ?.binaries
        ?.executable(compilation)
        ?.first { it.mode == mode } as? JsIrBinary

fun Project.createJsShellExec(
    config: BenchmarkConfiguration,
    target: BenchmarkTarget,
    compilation: KotlinJsCompilation,
    taskName: String
): TaskProvider<Exec> = tasks.register(taskName, Exec::class) {
    dependsOn(compilation.runtimeDependencyFiles)
    dependsOn(unzipJsShell)

    group = BenchmarksPlugin.BENCHMARKS_TASK_GROUP
    description = "Executes benchmark for '${target.name}' with jsShell"

    val newArgs = mutableListOf<String>()
    executable = File(unzipJsShell.get().destinationDir, "js").absolutePath

    newArgs.add("--wasm-gc")
    newArgs.add("--wasm-function-references")

    val productionBinary = tryGetBinary(compilation, KotlinJsBinaryMode.PRODUCTION) ?: error("Not found production binary")
    dependsOn(productionBinary.linkSyncTask)

    val inputFile = productionBinary.mainFile
    dependsOn(inputFile)
    val inputFileAsFile = inputFile.get().asFile
    workingDir = inputFileAsFile.parentFile
    if (compilation.target.platformType == KotlinPlatformType.wasm) {
        newArgs.add("--module=${inputFileAsFile.absolutePath}")
    } else {
        newArgs.add("--file=${inputFileAsFile.absolutePath}")
    }
    val reportFile = setupReporting(target, config)
    val jsShellReportFile = File(reportFile.parentFile, "jsShell_" + reportFile.name)
    newArgs.add("--")
    newArgs.add(writeParameters(target.name, jsShellReportFile, traceFormat(), config).absolutePath)
    args = newArgs
    standardOutput = ConsoleAndFilesOutputStream()
}


fun Project.createJsEngineBenchmarkExecTask(
    config: BenchmarkConfiguration,
    target: BenchmarkTarget,
    compilation: KotlinJsCompilation
) {
    val taskName = "jsShell_${target.name}${config.capitalizedName()}${BenchmarksPlugin.BENCHMARK_EXEC_SUFFIX}"
    val compilationTarget = compilation.target
    if (compilationTarget is KotlinWasmSubTargetContainerDsl) {
        check(compilation is KotlinJsCompilation) { "Legacy Kotlin/JS is does not supported by JsShell engine" }
        val execTask = createJsShellExec(config, target, compilation, taskName)
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
                val benchmarkCompilation = compilation.target.compilations.maybeCreate(target.name + BenchmarksPlugin.BENCHMARK_COMPILATION_SUFFIX) as KotlinJsCompilation
                createJsEngineBenchmarkExecTask(config, target, benchmarkCompilation)
            }
        }
    }
}

tasks.withType<KotlinJsCompile> {
    compilerOptions.freeCompilerArgs.add("-Xskip-prerelease-check")
}