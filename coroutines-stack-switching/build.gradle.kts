@file:OptIn(KotlinxBenchmarkPluginInternalApi::class, ExperimentalWasmDsl::class, KotlinxBenchmarkPluginExperimentalApi::class)

import kotlinx.benchmark.gradle.BenchmarksPlugin
import kotlinx.benchmark.gradle.CustomEngine
import kotlinx.benchmark.gradle.KotlinxBenchmarkPluginExperimentalApi
import kotlinx.benchmark.gradle.benchmark
import kotlinx.benchmark.gradle.internal.KotlinxBenchmarkPluginInternalApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.ir.ExecutableWasm
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.binaryen.BinaryenPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.d8.D8EnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.d8.D8Plugin

plugins {
    kotlin("multiplatform")
}

apply<BenchmarksPlugin>()

apply<NodeJsPlugin>()
the<NodeJsEnvSpec>().version.set(libs.versions.nodejs.get())
apply<BinaryenPlugin>()
the<BinaryenRootEnvSpec>().version.set(libs.versions.binaryen.get())
apply<D8Plugin>()
the<D8EnvSpec>().version.set(libs.versions.v8.get())

repositories {
    mavenCentral()
    maven(uri(rootProject.file("kotlin-compiler")))
    maven(uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/"))
    mavenLocal()
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        compilerOptions {
            freeCompilerArgs.add("-Xwasm-use-stack-switching-proposal")
        }
    }

    sourceSets {
        commonMain {
            // Reuse the root project's benchmark sources (shared macro infra + coroutines).
            kotlin.srcDir(rootProject.file("src/commonMain/kotlin/infra"))
            kotlin.srcDir(rootProject.file("src/commonMain/kotlin/coroutines"))
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(files(rootProject.file("kotlinx-benchmarks/kotlinx-benchmark-runtime-0.6.0.jar")))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val wasmJsMain by getting {
            // Reuse the root project's custom-engine (d8) support.
            kotlin.srcDir(rootProject.file("src/wasmJsMain/kotlin/customEngines"))
            dependencies {
                implementation(files(rootProject.file("kotlinx-benchmarks/kotlinx-benchmark-runtime-wasm-js-0.6.0.klib")))
            }
        }
    }
}

fun getBenchmarkOutputBinary(mode: KotlinJsBinaryMode): JsIrBinary {
    val compilations = kotlin.targets.getByName("wasmJs").compilations
    val benchmarkCompilation = compilations.single {
        it.compilationName.endsWith(BenchmarksPlugin.BENCHMARK_COMPILATION_SUFFIX)
    } as KotlinJsIrCompilation
    return benchmarkCompilation.binaries.single { it.mode == mode }
}

class StackSwitchingInput(val isProd: Boolean, val file: Provider<File>) {
    val engineName: String get() = "Wasm_${if (isProd) "Prod" else "Dev"}_D8"
}

val stackSwitchingInputs = listOf(
    StackSwitchingInput(isProd = true, file = provider { (getBenchmarkOutputBinary(KotlinJsBinaryMode.PRODUCTION) as ExecutableWasm).mainOptimizedFile.get().asFile }),
    StackSwitchingInput(isProd = false, file = provider { getBenchmarkOutputBinary(KotlinJsBinaryMode.DEVELOPMENT).mainFile.get().asFile }),
)
val stackSwitchingEngines = stackSwitchingInputs.map { input ->
    CustomEngine(
        name = input.engineName,
        enginePath = layout.file(the(D8EnvSpec::class).executable.map { File(it) }),
        engineArguments = input.file.map { listOf("--experimental-wasm-wasmfx", "--module", it.absolutePath, "--", "<ARGUMENTS>") },
    )
}

benchmark {
    configurations {
        stackSwitchingEngines.forEach { engine ->
            with(create("slow_${engine.name}")) {
                iterations = 1
                warmups = 5
                iterationTime = 1
                iterationTimeUnit = "nanos"
                outputTimeUnit = "millis"
                reportFormat = "json"
                mode = "avgt"
                advanced("jsUseBridge", true)
                includes.add("macroBenchmarks.coroutinesSlowBenchmarks")
                includes.add("macroBenchmarks.CoroutineMacroBenchmarks")
                advanced("wasmFork", "perBenchmark")
                customEngine = engine
            }
            with(create("fast_${engine.name}")) {
                iterations = 5
                warmups = 5
                iterationTime = 50
                iterationTimeUnit = "millis"
                outputTimeUnit = "millis"
                reportFormat = "json"
                mode = "avgt"
                advanced("jsUseBridge", true)
                includes.add("microBenchmarks.CoroutinesIntrinsicsBenchmark")
                advanced("wasmFork", "perBenchmark")
                customEngine = engine
            }
        }
    }
    targets {
        register("wasmJs")
    }
}

tasks.withType<KotlinJsCompile> {
    compilerOptions.freeCompilerArgs.add("-Xskip-prerelease-check")
}

tasks.withType<NodeJsExec>().configureEach {
    nodeArgs += "--experimental-wasm-wasmfx"
}
