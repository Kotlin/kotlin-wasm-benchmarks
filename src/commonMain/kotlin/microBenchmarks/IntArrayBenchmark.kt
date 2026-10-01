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
class IntArrayBenchmark {
    private lateinit var data: IntArray

    @Setup
    fun setup() {
        val list = IntArray(BENCHMARK_SIZE)
        var index = 0
        for (n in intValues(BENCHMARK_SIZE)) {
            list[index++] = n
        }

        data = list
    }

    @Benchmark
    fun copy(blackhole: Blackhole) {
        blackhole.consume(data.toList())
    }

    @Benchmark
    fun copyManual(blackhole: Blackhole) {
        val list = ArrayList<Int>(data.size)
        for (item in data) {
            list.add(item)
        }
        blackhole.consume(list)
    }

    @Benchmark
    fun filterAndCount(blackhole: Blackhole) {
        blackhole.consume(data.filter { filterLoad(it) }.count())
    }

    @Benchmark
    fun filterSomeAndCount(blackhole: Blackhole) {
        blackhole.consume(data.filter { filterSome(it) }.count())
    }

    @Benchmark
    fun filterAndMap(blackhole: Blackhole) {
        blackhole.consume(data.filter { filterLoad(it) }.map { mapLoad(it) })
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
        blackhole.consume(data.filter { filterLoad(it) })
    }

    @Benchmark
    fun filterSome(blackhole: Blackhole) {
        blackhole.consume(data.filter { filterSome(it) })
    }

    @Benchmark
    fun filterPrime(blackhole: Blackhole) {
        blackhole.consume(data.filter { filterPrime(it) })
    }

    @Benchmark
    fun filterManual(blackhole: Blackhole) {
        val list = ArrayList<Int>()
        for (it in data) {
            if (filterLoad(it))
                list.add(it)
        }
        blackhole.consume(list)
    }

    @Benchmark
    fun filterSomeManual(blackhole: Blackhole) {
        val list = ArrayList<Int>()
        for (it in data) {
            if (filterSome(it))
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
    fun countFilteredSomeManual(blackhole: Blackhole) {
        var count = 0
        for (it in data) {
            if (filterSome(it))
                count++
        }
        blackhole.consume(count)
    }

    @Benchmark
    fun countFilteredPrimeManual(blackhole: Blackhole) {
        var count = 0
        for (it in data) {
            if (filterPrime(it))
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
    fun countFilteredSome(blackhole: Blackhole) {
        val result = data.count { filterSome(it) }
        blackhole.consume(result)
    }

    @Benchmark
    fun countFilteredPrime(blackhole: Blackhole) {
        val result = data.count { filterPrime(it) }
        blackhole.consume(result)
    }

    @Benchmark
    fun countFilteredLocal(blackhole: Blackhole) {
        val result = data.cnt { filterLoad(it) }
        blackhole.consume(result)
    }

    @Benchmark
    fun countFilteredSomeLocal(blackhole: Blackhole) {
        val result = data.cnt { filterSome(it) }
        blackhole.consume(result)
    }

    @Benchmark
    fun reduce(blackhole: Blackhole) {
        val result = data.fold(0) { acc, it -> if (filterLoad(it)) acc + 1 else acc }
        blackhole.consume(result)
    }
}