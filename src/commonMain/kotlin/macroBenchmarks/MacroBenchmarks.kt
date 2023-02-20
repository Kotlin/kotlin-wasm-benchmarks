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

open class MacroBenchmarksBase {
    protected fun runBenchmark(macroBenchmark: MacroBenchmark) {
        check(macroBenchmark.innerBenchmarkLoop(macroBenchmark.defaultInnerIterations.max())) {
            "Failed bencmark ${macroBenchmark::class.simpleName}"
        }
    }
}

@State(Scope.Benchmark)
class MacroBenchmarksSlow : MacroBenchmarksBase() {
    @Benchmark
    fun cd() {
        runBenchmark(CD())
    }

    @Benchmark
    fun havlak() {
        runBenchmark(Havlak())
    }

    @Benchmark
    fun json() {
        runBenchmark(Json())
    }

    @Benchmark
    fun nBody() {
        runBenchmark(NBody())
    }

    @Benchmark
    fun mandelbrot() {
        runBenchmark(Mandelbrot())
    }
}

@State(Scope.Benchmark)
class MacroBenchmarksFast : MacroBenchmarksBase() {
    @Benchmark
    fun bounce() {
        runBenchmark(Bounce())
    }

    @Benchmark
    fun list() {
        runBenchmark(List())
    }

    @Benchmark
    fun permute() {
        runBenchmark(Permute())
    }

    @Benchmark
    fun queens() {
        runBenchmark(Queens())
    }

    @Benchmark
    fun sieve() {
        runBenchmark(Sieve())
    }

    @Benchmark
    fun storage() {
        runBenchmark(Storage())
    }

    @Benchmark
    fun towers() {
        runBenchmark(Towers())
    }
}