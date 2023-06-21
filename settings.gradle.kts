rootProject.name = "kotlin-wasm-benchmarks"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(uri("./kotlin-compiler"))
        maven(uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/"))
    }
    resolutionStrategy {
        repositories {
            maven(uri("./kotlin-compiler"))
            gradlePluginPortal()
            maven(uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/"))
        }
    }
    val kotlin_version: String by settings
    plugins {
        kotlin("multiplatform").version(kotlin_version)
    }
}