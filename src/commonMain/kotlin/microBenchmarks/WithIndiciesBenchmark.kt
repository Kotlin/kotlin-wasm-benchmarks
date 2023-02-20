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

package microBenchmarks

import kotlinx.benchmark.*

@State(Scope.Benchmark)
class WithIndiciesBenchmark {
    private lateinit var data: ArrayList<Value>

    @Setup
    fun setup() {
        val list = ArrayList<Value>(BENCHMARK_SIZE)
        for (n in classValues(BENCHMARK_SIZE)) {
            list.add(n)
        }
        data = list
    }

    @Benchmark
    fun withIndicies(blackhole: Blackhole) {
        for ((index, value) in data.withIndex()) {
            if (filterLoad(value)) {
                blackhole.consume(index)
                blackhole.consume(value)
            }
        }
    }

    @Benchmark
    fun withIndiciesManual(blackhole: Blackhole) {
        var index = 0
        for (value in data) {
            if (filterLoad(value)) {
                blackhole.consume(index)
                blackhole.consume(value)
            }
            index++
        }
    }
}
