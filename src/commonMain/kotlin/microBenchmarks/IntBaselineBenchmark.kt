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
class IntBaselineBenchmark {

    @Benchmark
    fun consume(blackhole: Blackhole) {
        for (item in 1..BENCHMARK_SIZE) {
            blackhole.consume(item)
        }
    }

    @Benchmark
    fun allocateList(): List<Int> =
        ArrayList<Int>(BENCHMARK_SIZE)

    @Benchmark
    fun allocateArray(): IntArray =
        IntArray(BENCHMARK_SIZE)

    //@Benchmark
    fun allocateListAndFill(): List<Int> {
        val list = ArrayList<Int>(BENCHMARK_SIZE)
        var item = 0
        while (item < BENCHMARK_SIZE) {
            list.add(item)
            item++
        }
        return list
    }

    @Benchmark
    fun allocateArrayAndFill(): IntArray {
        val list = IntArray(BENCHMARK_SIZE)
        var item = 0
        while (item < BENCHMARK_SIZE) {
            list[item] = item
            item++
        }
        return list
    }
}