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

@OptIn(ExperimentalUnsignedTypes::class)
@State(Scope.Benchmark)
class ForLoopsBenchmark {
    private lateinit var array: Array<Int>
    private lateinit var intArray: IntArray
    private lateinit var charArray: CharArray
    private lateinit var floatArray: FloatArray
    private lateinit var string: String
    private var uIntArray: UIntArray? = null
    private var uShortArray: UShortArray? = null
    private var uLongArray: ULongArray? = null

    @Setup
    fun setup() {
        array = Array(BENCHMARK_SIZE) {
            it
        }

        intArray = IntArray(BENCHMARK_SIZE) {
            it
        }

        charArray = CharArray(BENCHMARK_SIZE) {
            it.toChar()
        }

        string = charArray.joinToString()

        floatArray = FloatArray(BENCHMARK_SIZE) {
            it.toFloat()
        }

        uIntArray = UIntArray(BENCHMARK_SIZE) {
            it.toUInt()
        }

        uShortArray = UShortArray(BENCHMARK_SIZE) {
            it.toUShort()
        }

        uLongArray = ULongArray(BENCHMARK_SIZE) {
            it.toULong()
        }
    }

    @Benchmark
    fun arrayLoop(): Long {
        var sum = 0L
        for (e in array) {
            sum += e
        }
        return sum
    }

    @Benchmark
    fun intArrayLoop(): Long {
        var sum = 0L
        for (e in intArray) {
            sum += e
        }
        return sum
    }

    @Benchmark
    fun charArrayLoop(): Long {
        var sum = 0L
        for (e in charArray) {
            sum += e.code.toLong()
        }
        return sum
    }

    @Benchmark
    fun stringLoop(): Long {
        var sum = 0L
        for (e in string) {
            sum += e.hashCode()
        }
        return sum
    }

    @Benchmark
    fun floatArrayLoop(): Double {
        var sum = 0.0
        for (e in floatArray) {
            sum += e
        }
        return sum
    }

    @Benchmark
    fun uIntArrayLoop(): ULong {
        var sum: ULong = 0u
        for (e in uIntArray!!) {
            sum += e
        }
        return sum
    }

    @Benchmark
    fun uShortArrayLoop(): ULong {
        var sum: ULong = 0u
        for (e in uShortArray!!) {
            sum += e
        }
        return sum
    }

    @Benchmark
    fun uLongArrayLoop(): ULong {
        var sum: ULong = 0u
        for (e in uLongArray!!) {
            sum += e
        }
        return sum
    }

    // Iterations over .indices
    @Benchmark
    fun arrayIndicesLoop(): Long {
        var sum = 0L
        for (i in array.indices) {
            sum += array[i]
        }
        return sum
    }

    @Benchmark
    fun intArrayIndicesLoop(): Long {
        var sum = 0L
        for (i in intArray.indices) {
            sum += intArray[i]
        }
        return sum
    }

    @Benchmark
    fun charArrayIndicesLoop(): Long {
        var sum = 0L
        for (i in charArray.indices) {
            sum += charArray[i].code.toLong()
        }
        return sum
    }

    @Benchmark
    fun stringIndicesLoop(): Long {
        var sum = 0L
        for (i in string.indices) {
            sum += string[i].hashCode()
        }
        return sum
    }

    @Benchmark
    fun floatArrayIndicesLoop(): Double {
        var sum = 0.0
        for (i in floatArray.indices) {
            sum += floatArray[i]
        }
        return sum
    }

    @Benchmark
    fun uIntArrayIndicesLoop(): ULong {
        var sum: ULong = 0u
        for (i in uIntArray!!.indices) {
            sum += uIntArray!![i]
        }
        return sum
    }

    @Benchmark
    fun uShortArrayIndicesLoop(): ULong {
        var sum: ULong = 0u
        for (i in uShortArray!!.indices) {
            sum += uShortArray!![i]
        }
        return sum
    }

    @Benchmark
    fun uLongArrayIndicesLoop(): ULong {
        var sum: ULong = 0u
        for (i in uLongArray!!.indices) {
            sum += uLongArray!![i]
        }
        return sum
    }
}