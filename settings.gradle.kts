rootProject.name = "kotlin-wasm-benchmarks"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(uri("./kotlin-compiler"))
    }
    resolutionStrategy {
        repositories {
            maven(uri("./kotlin-compiler"))
            gradlePluginPortal()
        }
    }
    val kotlin_version: String by settings
    plugins {
        kotlin("multiplatform").version(kotlin_version)
    }
}