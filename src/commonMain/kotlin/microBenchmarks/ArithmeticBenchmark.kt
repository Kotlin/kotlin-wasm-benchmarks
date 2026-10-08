/*
 * Copyright 2025 JetBrains s.r.o.
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

package microBenchmarks

import kotlinx.benchmark.*

@State(Scope.Benchmark)
class ArithmeticBenchmark {
    @Benchmark
    fun division(blackhole: Blackhole) {
        var i = 1
        var j = 1
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234) / i / j / i / j / i / j
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun division_constant(blackhole: Blackhole) {
        var i = 1
        var j = 1
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234) / 42 / i / 42 / j / 42 / i
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun remainder(blackhole: Blackhole) {
        var i = 1
        var j = 1
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234) % i % j % i % j % i % j
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun remainder_constant(blackhole: Blackhole) {
        var i = 1
        var j = 1
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234) % 42 % i % 42 % j % 42 % i
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun float_division(blackhole: Blackhole) {
        var i = 1
        var j = 1f
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234).toFloat() / i / j / i / j / i / j
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun float_division_constant(blackhole: Blackhole) {
        var i = 1
        var j = 1f
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234).toFloat() / 42 / i / 42 / j / 42 / i
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun float_remainder(blackhole: Blackhole) {
        var i = 1
        var j = 1f
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234).toFloat() % i % j % i % j % i % j
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun float_remainder_constant(blackhole: Blackhole) {
        var i = 1
        var j = 1f
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234).toFloat() % 42 % i % 42 % j % 42 % i
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun double_division(blackhole: Blackhole) {
        var i = 1
        var j = 1.0
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234).toFloat() / i / j / i / j / i / j
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun double_division_constant(blackhole: Blackhole) {
        var i = 1
        var j = 1.0
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234).toDouble() / 42 / i / 42 / j / 42 / i
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun double_remainder(blackhole: Blackhole) {
        var i = 1
        var j = 1.0
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234).toDouble() % i % j % i % j % i % j
            i++
        }
        blackhole.consume(j)
    }

    @Benchmark
    fun double_remainder_constant(blackhole: Blackhole) {
        var i = 1
        var j = 1.0
        while (i < BENCHMARK_SIZE) {
            j += (i shl 1234).toDouble() % 42 % i % 42 % j % 42 % i
            i++
        }
        blackhole.consume(j)
    }
}