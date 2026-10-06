/*
 * Copyright 2024 JetBrains s.r.o.
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
import kotlin.math.sign

@State(Scope.Benchmark)
class IntegerSignBenchmark {
    private lateinit var intValues: IntArray
    private lateinit var longValues: LongArray

    @Setup
    fun setup() {
        var currentIntValue = 1
        intValues = IntArray(BENCHMARK_SIZE) {
            // see https://en.wikipedia.org/wiki/Linear_congruential_generator#Parameters_in_common_use
            currentIntValue = currentIntValue * 1664525 + 1013904223
            currentIntValue
        }

        var currentLongValue = 1L
        longValues = LongArray(BENCHMARK_SIZE) {
            // see https://en.wikipedia.org/wiki/Linear_congruential_generator#Parameters_in_common_use
            currentLongValue = currentLongValue * 6364136223846793005 + 1442695040888963407
            currentLongValue
        }
    }

    @Benchmark
    fun intSign(blackhole: Blackhole) {
        var result = 0
        for (value in intValues) {
            result += value.sign
        }
        blackhole.consume(result)
    }

    @Benchmark
    fun longSign(blackhole: Blackhole) {
        var result = 0
        for (value in longValues) {
            result += value.sign
        }
        blackhole.consume(result)
    }
}
