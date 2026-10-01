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
class ClassArrayBenchmark {
    private lateinit var data: Array<Value>

    @Setup
    fun setup() {
        val list = ArrayList<Value>(BENCHMARK_SIZE)
        for (n in classValues(BENCHMARK_SIZE)) {
            list.add(n)
        }
        data = list.toTypedArray()
    }

    @Benchmark
    fun copy(blackhole: Blackhole) {
        val result = data.toList()
        blackhole.consume(result)
    }

    @Benchmark
    fun copyManual(blackhole: Blackhole) {
        val data = data
        val list = ArrayList<Value>(data.size)
        for (item in data) {
            list.add(item)
        }
        blackhole.consume(list)
    }

    @Benchmark
    fun filterAndCount(blackhole: Blackhole) {
        val result = data.filter { filterLoad(it) }.count()
        blackhole.consume(result)
    }

    @Benchmark
    fun filterAndMap(blackhole: Blackhole) {
        val result = data.filter { filterLoad(it) }.map { mapLoad(it) }
        blackhole.consume(result)
    }

    @Benchmark
    fun filterAndMapManual(blackhole: Blackhole) {
        val list = ArrayList<String>()
        for (it in data) {
            if (filterLoad(it)) {
                val value = mapLoad(it)
                list.add(value)
            }
        }
        blackhole.consume(list)
    }

    @Benchmark
    fun filter(blackhole: Blackhole) {
        val result = data.filter { filterLoad(it) }
        blackhole.consume(result)
    }

    @Benchmark
    fun filterManual(blackhole: Blackhole) {
        val list = ArrayList<Value>()
        for (it in data) {
            if (filterLoad(it))
                list.add(it)
        }
        blackhole.consume(list)
    }

    @Benchmark
    fun countFilteredManual(blackhole: Blackhole) {
        var count = 0
        for (it in data) {
            if (filterLoad(it))
                count++
        }
        blackhole.consume(count)
    }

    @Benchmark
    fun countFiltered(blackhole: Blackhole) {
        val result = data.count { filterLoad(it) }
        blackhole.consume(result)
    }

    @Benchmark
    fun countFilteredLocal(blackhole: Blackhole) {
        val result = data.cnt { filterLoad(it) }
        blackhole.consume(result)
    }

//    @Benchmark
//    fun reduce(blackhole: Blackhole) {
//        val result = data.fold(0) { acc, it -> if (filterLoad(it)) acc + 1 else acc }
//        blackhole.consume(result)
//    }
}
