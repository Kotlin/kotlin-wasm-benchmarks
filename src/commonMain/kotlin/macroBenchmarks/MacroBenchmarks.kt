/*
 * Copyright 2023 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package macroBenchmarks

import kotlinx.benchmark.*
import macroBenchmarks.coroutinesSlowBenchmarks.Coroutines

@State(Scope.Benchmark)
class MacroBenchmarksSlow : MacroBenchmarksBase() {
    @Benchmark
    fun cd(blackhole: Blackhole) {
        runBenchmark(CD(), blackhole)
    }

    @Benchmark
    fun havlak(blackhole: Blackhole) {
        runBenchmark(Havlak(), blackhole)
    }

    @Benchmark
    fun json(blackhole: Blackhole) {
        runBenchmark(Json(), blackhole)
    }

    @Benchmark
    fun nBody(blackhole: Blackhole) {
        runBenchmark(NBody(), blackhole)
    }

    @Benchmark
    fun mandelbrot(blackhole: Blackhole) {
        runBenchmark(Mandelbrot(), blackhole)
    }

    @Benchmark
    fun coroutineIteration(blackhole: Blackhole) {
        runBenchmark(Coroutines.Iteration(), blackhole)
    }

    @Benchmark
    fun coroutineRecursion(blackhole: Blackhole) {
        runBenchmark(Coroutines.Recursion(), blackhole)
    }
}

@State(Scope.Benchmark)
class MacroBenchmarksFast : MacroBenchmarksBase() {
    @Benchmark
    fun bounce(blackhole: Blackhole) {
        runBenchmark(Bounce(), blackhole)
    }

    @Benchmark
    fun list(blackhole: Blackhole) {
        runBenchmark(List(), blackhole)
    }

    @Benchmark
    fun permute(blackhole: Blackhole) {
        runBenchmark(Permute(), blackhole)
    }

    @Benchmark
    fun queens(blackhole: Blackhole) {
        runBenchmark(Queens(), blackhole)
    }

    @Benchmark
    fun sieve(blackhole: Blackhole) {
        runBenchmark(Sieve(), blackhole)
    }

    @Benchmark
    fun storage(blackhole: Blackhole) {
        runBenchmark(Storage(), blackhole)
    }

    @Benchmark
    fun towers(blackhole: Blackhole) {
        runBenchmark(Towers(), blackhole)
    }
}
