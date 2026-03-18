@file:OptIn(KotlinxBenchmarkPluginInternalApi::class, ExperimentalWasmDsl::class, KotlinxBenchmarkPluginExperimentalApi::class)

import de.undercouch.gradle.tasks.download.Download
import kotlinx.benchmark.gradle.BenchmarksPlugin
import kotlinx.benchmark.gradle.CustomEngine
import kotlinx.benchmark.gradle.KotlinxBenchmarkPluginExperimentalApi
import kotlinx.benchmark.gradle.benchmark
import kotlinx.benchmark.gradle.internal.KotlinxBenchmarkPluginInternalApi
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.ir.ExecutableWasm
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.d8.D8EnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.d8.D8Plugin

buildscript {
    repositories {
        gradlePluginPortal()
        maven(uri("./kotlin-compiler"))
//        mavenLocal()
    }

    val kotlin_version: String by project

    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:0.4.17")
//        classpath("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:0.5.0-SNAPSHOT")
//        classpath(files("./kotlinx-benchmarks/kotlinx-benchmark-plugin-0.5.0.jar"))
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
    version.set(libs.versions.nodejs.get())
}

apply<BinaryenPlugin>()
the<BinaryenRootEnvSpec>().version.set(libs.versions.binaryen.get())

apply<D8Plugin>()
the<D8EnvSpec>().version.set(libs.versions.v8.get())

allprojects.forEach {
    it.tasks.withType<KotlinNpmInstallTask>().configureEach {
        args.add("--ignore-engines")
    }
}

repositories {
    mavenCentral()
    maven(uri("./kotlin-compiler"))
    maven(uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/"))
    mavenLocal()
}

kotlin {
    js {
        this as KotlinJsIrTarget
        //d8()
        nodejs()
    }

    wasmJs("wasm") {
        nodejs()
        //nodejs()
    }
    wasmWasi("wasmWasi") {
        nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
//                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.5.0-SNAPSHOT")
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.17")
//                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-0.5.0.jar"))
            }
        }

        val wasmMain by getting {
            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime-wasm-js:0.5.0-SNAPSHOT")
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime-wasm-js:0.4.17")
//                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-wasm-js-0.5.0.klib"))
            }
        }

        val wasmWasiMain by getting {
            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime-wasm-wasi:0.5.0-SNAPSHOT")
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime-wasm-wasi:0.4.17")
//                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-wasm-wasi-0.5.0.klib"))
            }
        }

        val jsMain by getting {
            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime-js:0.5.0-SNAPSHOT")
                implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime-js:0.4.17")
//                implementation(files("./kotlinx-benchmarks/kotlinx-benchmark-runtime-js-0.5.0.klib"))
            }
        }
    }
}

fun getBenchmarkOutputBinary(mode: KotlinJsBinaryMode): JsIrBinary {
    val wasiCompilations = kotlin.targets.getByName("wasmWasi").compilations
    val benchmarkCompilation = wasiCompilations.single {
        it.compilationName.endsWith(BenchmarksPlugin.BENCHMARK_COMPILATION_SUFFIX)
    } as KotlinJsIrCompilation
    return benchmarkCompilation.binaries.single { it.mode == mode }
}

val wasmWasiProductionOutputFile = layout.file(provider {
    val binary = getBenchmarkOutputBinary(KotlinJsBinaryMode.PRODUCTION)
    val mainFile = (binary as ExecutableWasm).mainOptimizedFile.get().asFile
    mainFile.resolveSibling("${mainFile.nameWithoutExtension}.wasm")
})
val wasmWasiDevelopmentOutputFile = layout.file(provider {
    val binary = getBenchmarkOutputBinary(KotlinJsBinaryMode.DEVELOPMENT)
    val mainFile = binary.mainFile.get().asFile
    mainFile.resolveSibling("${mainFile.nameWithoutExtension}.wasm")
})
val wasmWasiOutputFiles = listOf("Production" to wasmWasiProductionOutputFile, "Development" to wasmWasiDevelopmentOutputFile)


val customEngines = mutableListOf<CustomEngine>()
val toolsDirectory = layout.buildDirectory.dir("tools")

///////////////////////////////// JS SHELL /////////////////////////////////////////////////////////
val jsShellVersion = libs.versions.jsShell.get()
val jsShellSuffix = when (currentOsType) {
    OsType(OsName.LINUX, OsArch.X86_32) -> "linux-i686"
    OsType(OsName.LINUX, OsArch.X86_64) -> "linux-x86_64"
    OsType(OsName.MAC, OsArch.X86_64),
    OsType(OsName.MAC, OsArch.ARM64) -> "mac"
    OsType(OsName.WINDOWS, OsArch.X86_32) -> "win32"
    OsType(OsName.WINDOWS, OsArch.X86_64) -> "win64"
    else -> error("unsupported os type $currentOsType")
}

fun getDirectoryForStrings(vararg names: String): String = names.joinToString("_") { it.replace(".", "_") }

val jsShellDownloadDirectory = "https://archive.mozilla.org/pub/firefox/releases/$jsShellVersion/jsshell"
val jsShellFileName = "jsshell-$jsShellSuffix.zip"
val jsShellDirectory = toolsDirectory.map { it.dir(getDirectoryForStrings("JsShell", jsShellSuffix, jsShellVersion)).asFile }
val jsShellUnpackedDirectory = jsShellDirectory.map { it.resolve(jsShellFileName) }
val jsShellDownload = tasks.register("jsShellDownload", Download::class) {
    src("$jsShellDownloadDirectory/$jsShellFileName")
    dest(jsShellUnpackedDirectory)
    overwrite(false)
}

val unzipJsShell = tasks.register("unzipJsShell", Copy::class) {
    dependsOn(jsShellDownload)
    from(zipTree(jsShellDownload.map { it.dest } ))
    into(jsShellDownload.map { it.dest.resolveSibling("unpacked") })
}

customEngines.add(
    CustomEngine(
        name = "JsShell",
        enginePath = layout.file(unzipJsShell.map { it.destinationDir.resolve("js") }),
        engineArguments = provider { listOf("--module", "<MODULE>", "--", "<ARGUMENTS>") }
    )
)

///////////////////////////////// WASM EDGE /////////////////////////////////////////////////////////
val wasmEdgeVersion = libs.versions.wasmedge.get()
val wasmEdgeSuffix = when (currentOsType) {
    OsType(OsName.LINUX, OsArch.X86_64) -> "manylinux_2_28_x86_64.tar.gz"
    OsType(OsName.MAC, OsArch.X86_64) -> "darwin_x86_64.tar.gz"
    OsType(OsName.MAC, OsArch.ARM64) -> "darwin_arm64.tar.gz"
    OsType(OsName.WINDOWS, OsArch.X86_32),
    OsType(OsName.WINDOWS, OsArch.X86_64) -> "windows.zip"
    else -> error("unsupported os type $currentOsType")
}

val wasmEdgeDownloadDirectory = "https://github.com/WasmEdge/WasmEdge/releases/download/$wasmEdgeVersion"
val wasmEdgeFileName = "WasmEdge-$wasmEdgeVersion-$wasmEdgeSuffix"
val wasmEdgeDirectory = toolsDirectory.map { it.dir(getDirectoryForStrings("WasmEdge", wasmEdgeVersion, wasmEdgeSuffix)).asFile }
val wasmEdgeUnpackedDirectory = wasmEdgeDirectory.map { it.resolve(wasmEdgeFileName) }
val wasmEdgeDownload = tasks.register("wasmEdgeDownload", Download::class) {
    src("$wasmEdgeDownloadDirectory/$wasmEdgeFileName")
    dest(wasmEdgeUnpackedDirectory)
    overwrite(false)
}

val unzipWasmEdge = tasks.register("unzipWasmEdge", UnzipWasmEdge::class) {
    from.setFrom(wasmEdgeDownload.map { it.dest })

    val currentOsTypeForConfigurationCache = currentOsType.name

    into.fileProvider(wasmEdgeDownload.map { it.dest.resolveSibling("unpacked") })

    getIsWindows.set(currentOsTypeForConfigurationCache !in setOf(OsName.MAC, OsName.LINUX))
    getIsMac.set(currentOsTypeForConfigurationCache == OsName.MAC)
}

wasmWasiOutputFiles.forEach { (mode, file) ->
    customEngines.add(
        CustomEngine(
            name = "WasmEdge$mode",
            enginePath = unzipWasmEdge.flatMap { it.into.dir("bin").map { dir -> dir.file("wasmedge") } },
            engineArguments = file.map { listOf("--dir", "/:/", it.asFile.absolutePath, "entryPointStub", "<ARGUMENTS>") }
        )
    )
}

///////////////////////////////// JSC /////////////////////////////////////////////////////////
val jscOsDependentClassifier = when (currentOsType.name) {
    OsName.MAC -> "tahoe"
    OsName.LINUX -> "linux64"
    OsName.WINDOWS -> "win64"
    else -> error("unsupported os type $currentOsType")
}

val jscOsDependentRevision = when (currentOsType.name) {
    OsName.MAC -> libs.versions.jscTahoe
    OsName.LINUX -> libs.versions.jscLinux
    OsName.WINDOWS -> libs.versions.jscWindows
    else -> error("unsupported os type $currentOsType")
}.get()

val jscDownloadDirectory = "https://packages.jetbrains.team/files/p/kt/kotlin-file-dependencies/javascriptcore"
val jscFileName = "${jscOsDependentClassifier}_$jscOsDependentRevision.zip"
val jscDirectory = toolsDirectory.map { it.dir(getDirectoryForStrings("jsc", jscOsDependentClassifier, jscOsDependentRevision)).asFile }
val jscUnpackedDirectory = jscDirectory.map { it.resolve(jscFileName) }
val jscDownload = tasks.register("jscDownload", Download::class) {
    src("$jscDownloadDirectory/$jscFileName")
    dest(jscUnpackedDirectory)
    overwrite(false)
}

val unzipJsc = tasks.register("unzipJsc", UnzipJsc::class) {
    from.setFrom(jscDownload.map { it.dest })

    into.fileProvider(jscDownload.map { it.dest.resolveSibling("unpacked") })

    val isLinux = currentOsType.name == OsName.LINUX
    getIsLinux.set(isLinux)
}

val createJscRunner = tasks.register("createJscRunner", CreateJscRunner::class) {
    osTypeName.set(currentOsType.name)

    val runnerFileName = if (currentOsType.name == OsName.WINDOWS) "runJsc.cmd" else "runJsc"
    val runnerFilePath = jscDirectory.map { it.resolve(runnerFileName) }
    outputFile.fileProvider(runnerFilePath)

    inputDirectory.set(unzipJsc.flatMap { it.into })
}
customEngines.add(
    CustomEngine(
        name = "JSC",
        enginePath = createJscRunner.flatMap { it.outputFile },
        engineArguments = provider { listOf("<MODULE>",  "--", "<ARGUMENTS>") }
    )
)

///////////////////////////////// WASM TIME /////////////////////////////////////////////////////////
val wasmtimeVersion = libs.versions.wasmtime.get()
val wasmtimePlatformSuffix = when (currentOsType) {
    OsType(OsName.LINUX, OsArch.X86_64) -> "x86_64-linux"
    OsType(OsName.MAC, OsArch.X86_64) -> "x86_64-macos"
    OsType(OsName.MAC, OsArch.ARM64) -> "aarch64-macos"
    OsType(OsName.WINDOWS, OsArch.X86_32),
    OsType(OsName.WINDOWS, OsArch.X86_64) -> "x86_64-windows"
    else -> error("unsupported os type $currentOsType")
}
val wasmtimeSuffix = when (currentOsType.name) {
    OsName.LINUX -> "tar.xz"
    OsName.MAC -> "tar.xz"
    OsName.WINDOWS -> "zip"
    else -> error("unsupported os type $currentOsType")
}

val wasmTimeDownloadDirectory = "https://github.com/bytecodealliance/wasmtime/releases/download/v$wasmtimeVersion"
val wasmTimeFileName = "wasmtime-v$wasmtimeVersion-$wasmtimePlatformSuffix.$wasmtimeSuffix"
val wasmTimeDirectory = toolsDirectory.map { it.dir(getDirectoryForStrings("WasmTime", wasmtimeVersion, wasmtimePlatformSuffix)).asFile }
val wasmTimeUnpackedDirectory = wasmTimeDirectory.map { it.resolve(wasmTimeFileName) }
val wasmTimeDownload = tasks.register("wasmTimeDownload", Download::class) {
    src("$wasmTimeDownloadDirectory/$wasmTimeFileName")
    dest(wasmTimeUnpackedDirectory)
    overwrite(false)
}

val unzipWasmtime = tasks.register("unzipWasmtime", UnzipWasmtime::class) {
    from.setFrom(wasmTimeDownload.map { it.dest })

    val currentOsTypeForConfigurationCache = currentOsType.name

    getIsWindows.set(currentOsTypeForConfigurationCache !in setOf(OsName.MAC, OsName.LINUX))

    into.set(project.layout.dir(wasmTimeDownload.map { it.dest.resolveSibling("unpacked") }))
}

wasmWasiOutputFiles.forEach { (mode, file) ->
    customEngines.add(
        CustomEngine(
            name = "WasmTime$mode",
            enginePath = unzipWasmtime.flatMap {
                it.into.dir("wasmtime-v$wasmtimeVersion-$wasmtimePlatformSuffix").map { dir -> dir.file("wasmtime") }
            },
            engineArguments = file.map {
                listOf("-W", "gc,exceptions,function-references", "--dir=/", it.asFile.absolutePath, "STUB", "<ARGUMENTS>")
            }
        )
    )
}

///////////////////////////////// V8 /////////////////////////////////////////////////////////
customEngines.add(
    CustomEngine(
        name = "D8",
        enginePath = layout.file(the(D8EnvSpec::class).executable.map { File(it) }),
        engineArguments = provider { listOf("--module", "<MODULE>", "--", "<ARGUMENTS>") }
    )
)

benchmark {
    configurations {
        customEngines.forEach { engine ->
            with(create("fastMacro${engine.name}")) {
                iterations = 5
                warmups = 5
                iterationTime = 50
                iterationTimeUnit = "millis"
                outputTimeUnit = "millis"
                reportFormat = "json"
                mode = "avgt"
                advanced("jsUseBridge", true)
                includes.add("macroBenchmarks.MacroBenchmarksFast")
                advanced("wasmFork", "perBenchmark")
                customEngine = engine
            }
            with(create("slowMacro${engine.name}")) {
                iterations = 1
                warmups = 5
                iterationTime = 1
                iterationTimeUnit = "nanos"
                outputTimeUnit = "millis"
                reportFormat = "json"
                mode = "avgt"
                advanced("jsUseBridge", true)
                includes.add("macroBenchmarks.MacroBenchmarksSlow")
                advanced("wasmFork", "perBenchmark")
                customEngine = engine
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
                "microBenchmarks.StringBenchmark.subSequence",
            )
            with(create("fastMicro${engine.name}")) {
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
                advanced("wasmFork", "perBenchmark")
                customEngine = engine
            }
            with(create("slowMicro${engine.name}")) {
                iterations = 1
                warmups = 5
                iterationTime = 1
                iterationTimeUnit = "nanos"
                outputTimeUnit = "millis"
                reportFormat = "json"
                mode = "avgt"
                advanced("jsUseBridge", true)
                includes.addAll(slowMicroBenchmarks)
                advanced("wasmFork", "perBenchmark")
                customEngine = engine
            }
        }
    }
    targets {
        register("js")
        register("wasmWasi")
        register("wasm")
    }
}


tasks.withType<KotlinJsCompile> {
    compilerOptions.freeCompilerArgs.add("-Xskip-prerelease-check")
}

rootProject.the<YarnRootExtension>().yarnLockMismatchReport =
    YarnLockMismatchReport.NONE