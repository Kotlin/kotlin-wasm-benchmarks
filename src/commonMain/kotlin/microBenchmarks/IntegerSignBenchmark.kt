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
    private var intValue = 1
    private var longValue = 1L

    fun nextInt(): Int {
        val r = intValue
        // see https://en.wikipedia.org/wiki/Linear_congruential_generator#Parameters_in_common_use
        intValue = intValue * 1664525 + 1013904223
        return r
    }

    fun nextLong(): Long {
        val r = longValue
        // see https://en.wikipedia.org/wiki/Linear_congruential_generator#Parameters_in_common_use
        longValue = longValue * 6364136223846793005 + 1442695040888963407
        return r
    }

    @Benchmark
    fun intSign(blackhole: Blackhole) {
        blackhole.consume(nextInt().sign)
    }

    @Benchmark
    fun longSign(blackhole: Blackhole) {
        blackhole.consume(nextLong().sign)
    }
}
