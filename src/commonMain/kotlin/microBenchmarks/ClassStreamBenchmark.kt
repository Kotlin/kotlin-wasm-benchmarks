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
class ClassStreamBenchmark {
    private lateinit var data: Iterable<Value>

    @Setup
    fun setup() {
        data = classValues(BENCHMARK_SIZE)
    }

    @Benchmark
    fun copy(blackhole: Blackhole) {
        val result = data.asSequence().toList()
        blackhole.consume(result)
    }

    @Benchmark
    fun copyManual(blackhole: Blackhole) {
        val list = ArrayList<Value>()
        for (item in data.asSequence()) {
            list.add(item)
        }
        blackhole.consume(list)
    }

    @Benchmark
    fun filterAndCount(blackhole: Blackhole) {
        val result = data.asSequence().filter { filterLoad(it) }.count()
        blackhole.consume(result)
    }

    @Benchmark
    fun filterAndMap(blackhole: Blackhole) {
        for (item in data.asSequence().filter { filterLoad(it) }.map { mapLoad(it) })
            blackhole.consume(item)
    }

    @Benchmark
    fun filterAndMapManual(blackhole: Blackhole) {
        for (it in data.asSequence()) {
            if (filterLoad(it)) {
                val item = mapLoad(it)
                blackhole.consume(item)
            }
        }
    }

    @Benchmark
    fun filter(blackhole: Blackhole) {
        for (item in data.asSequence().filter { filterLoad(it) })
            blackhole.consume(item)
    }

    @Benchmark
    fun filterManual(blackhole: Blackhole){
        for (it in data.asSequence()) {
            if (filterLoad(it))
                blackhole.consume(it)
        }
    }

    @Benchmark
    fun countFilteredManual(blackhole: Blackhole) {
        var count = 0
        for (it in data.asSequence()) {
            if (filterLoad(it))
                count++
        }
        blackhole.consume(count)
    }

    @Benchmark
    fun countFiltered(blackhole: Blackhole) {
        val result = data.asSequence().count { filterLoad(it) }
        blackhole.consume(result)
    }

//    @Benchmark
//    fun countFilteredLocal(blackhole: Blackhole) {
//        val result = data.asSequence().cnt { filterLoad(it) }
//        blackhole.consume(result)
//    }

    @Benchmark
    fun reduce(blackhole: Blackhole) {
        val result = data.asSequence().fold(0) {acc, it -> if (filterLoad(it)) acc + 1 else acc }
        blackhole.consume(result)
    }
}