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
class ArrayCopyBenchmark {
    class CustomArray<T>(capacity: Int = 0) {
        private var hashes: IntArray = IntArray(capacity)
        @Suppress("UNCHECKED_CAST")
        private var values: Array<T?> = arrayOfNulls<Any>(capacity) as Array<T?>
        private var _size: Int = 0

        fun add(index: Int, element: T): Boolean {
            val oldSize = _size

            // Grow the array if needed.
            if (oldSize == hashes.size) {
                val newSize = if (oldSize > 0) oldSize * 2 else 2
                hashes = hashes.copyOf(newSize)
                values = values.copyOf(newSize)
            }

            // Shift the array if needed.
            if (index < oldSize) {
                hashes.copyInto(
                        hashes,
                        destinationOffset = index + 1,
                        startIndex = index,
                        endIndex = oldSize
                )
                values.copyInto(
                        values,
                        destinationOffset = index + 1,
                        startIndex = index,
                        endIndex = oldSize
                )
            }

            hashes[index] = element.hashCode()
            values[index] = element

            _size++
            return true
        }
    }

    // private lateinit var array: CustomArray<Int>
    private val end = 2 * BENCHMARK_SIZE
    // @Setup
    // fun setup() {
    //     // array = CustomArray()
    //     // end = 2 * BENCHMARK_SIZE
    // }

    @Benchmark
    fun copyInSameArray(): CustomArray<Int> {
        val array = CustomArray<Int>()
        val end = end
        var i = 0
        while (i < end) {
            array.add(0, i)
            i++
        }
        return array
    }
}
