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

// Benchmark from KT-46482.
@State(Scope.Benchmark)
class ChainableBenchmark {

    class IntArrayList(capacity: Int = 7) {
        private var data: IntArray = IntArray(capacity)
        var size: Int = 0
            private set
        val capacity get() = data.size
        private fun grow(minSize: Int) {
            data = data.copyOf(kotlin.math.max(minSize, capacity * 3))
        }
        private fun ensure(count: Int) {
            if (size + count >= capacity) {
                grow(size + count)
            }
        }

        fun add(value: Int) {
            ensure(1)
            data[size++] = value
        }

        fun addChainable(value: Int): IntArrayList {
            add(value)
            return this
        }

        operator fun get(index: Int): Int = data[index]
        operator fun set(index: Int, value: Int) {
            data[index] = value
        }
    }
    val size = BENCHMARK_SIZE * 100

    @Benchmark
    fun testChainable() {
        val list = IntArrayList()
        for (i in 0..size) {
            list.addChainable(i)
        }
        for (i in 0..size) {
            list[i] = i * 2
        }
        var sum = 0
        for (i in 0..size) {
            sum += list[i]
        }
    }
}
