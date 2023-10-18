buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.20-RC")
    }
}

plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20-RC"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}