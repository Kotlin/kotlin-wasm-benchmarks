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
class IntStreamBenchmark {
    private lateinit var data: Iterable<Int>

    @Setup
    fun setup() {
        data = intValues(BENCHMARK_SIZE)
    }
    
    //@Benchmark
    fun copy(): List<Int> {
        return data.asSequence().toList()
    }
    
    @Benchmark
    fun copyManual(): List<Int> {
        val list = ArrayList<Int>()
        for (item in data.asSequence()) {
            list.add(item)
        }
        return list
    }
    
    @Benchmark
    fun filterAndCount(): Int {
        return data.asSequence().filter { filterLoad(it) }.count()
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
    fun filterManual(blackhole: Blackhole) {
        for (it in data.asSequence()) {
            if (filterLoad(it))
                blackhole.consume(it)
        }
    }
    
    @Benchmark
    fun countFilteredManual(): Int {
        var count = 0
        for (it in data.asSequence()) {
            if (filterLoad(it))
                count++
        }
        return count
    }
    
    @Benchmark
    fun countFiltered(): Int {
        return data.asSequence().count { filterLoad(it) }
    }
    
    @Benchmark
    fun countFilteredLocal(): Int {
        return data.asSequence().cnt { filterLoad(it) }
    }
    
    @Benchmark
    fun reduce(): Int {
        return data.asSequence().fold(0) {acc, it -> if (filterLoad(it)) acc + 1 else acc }
    }
}