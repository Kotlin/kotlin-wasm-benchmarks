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
class LoopBenchmark {
    lateinit var arrayList: List<Value>
    lateinit var array: Array<Value>

    @Setup
    fun setup() {
        val list = ArrayList<Value>(BENCHMARK_SIZE)
        for (n in classValues(BENCHMARK_SIZE))
            list.add(n)
        arrayList = list
        array = list.toTypedArray()
    }

    @Benchmark 
    fun arrayLoop(blackhole: Blackhole) {
        for (x in array) {
            blackhole.consume(x)
        }
    }

    @Benchmark 
    fun arrayIndexLoop(blackhole: Blackhole) {
        for (i in array.indices) {
            blackhole.consume(array[i])
        }
    }

    @Benchmark 
    fun rangeLoop(blackhole: Blackhole) {
        for (i in 0..BENCHMARK_SIZE) {
            blackhole.consume(i)
        }
    }

    @Benchmark 
    fun arrayListLoop(blackhole: Blackhole) {
        for (x in arrayList) {
            blackhole.consume(x)
        }
    }

    @Benchmark 
    fun arrayWhileLoop(blackhole: Blackhole) {
        var i = 0
        val s = array.size
        while (i < s) {
            blackhole.consume(array[i])
            i++
        }
    }

    @Benchmark 
    fun arrayForeachLoop(blackhole: Blackhole) {
        array.forEach { blackhole.consume(it) }
    }

    @Benchmark 
    fun arrayListForeachLoop(blackhole: Blackhole) {
        arrayList.forEach { blackhole.consume(it) }
    }
}