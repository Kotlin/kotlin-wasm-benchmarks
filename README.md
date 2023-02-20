# Kotlin Wasm Benchmarks

This is kotlin multiplatform benchmarks to compare Kotlin Wasm vs Kotlin JS performance.

# Description
These benchmarks are based on JetBrains micro-benchmarks benchmarks and macro-benchmarks based on [are-we-fast-yet](https://github.com/smarr/are-we-fast-yet) benchmarks collection.
To perform benchmarks it uses [kotlinx-benchmarks](https://github.com/Kotlin/kotlinx-benchmark) library.

# Build and Run
Specify Kotlin version in `gradle.properties` file or use additional gradle argument `-Pkotlin_version=1.8.0`.

### To run benchmarks with Wasm:

`./gradlew wasmBenchmark`

### To run benchmarks with [Binaryen](https://github.com/WebAssembly/binaryen) optimised Wasm:

`./gradlew wasmOptBenchmark`

### To run benchmarks with JS:

`./gradlew jsBenchmark`

### Result output is located in `build/reports` directory.

# License
See LICENSE.md file for details.