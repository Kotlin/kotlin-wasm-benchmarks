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
import kotlin.math.roundToInt
import kotlin.math.sqrt

@State(Scope.Benchmark)
class StringBenchmark {
    private var csv: String = ""
    private val subSequenceRanges = mutableListOf<Pair<Int, Int>>()

    @Setup
    fun setup() {
        for (i in 1 until BENCHMARK_SIZE) {
            val elem = Random.nextDouble()
            csv += elem
            csv += ","
        }
        csv += 0.0

        for (i in 1..sqrt(BENCHMARK_SIZE.toDouble()).roundToInt()) {
            for (j in 0..csv.length - i) {
                subSequenceRanges.add(Pair(j, j + i))
            }
        }
    }
    
    @Benchmark
    fun stringConcat(): String? {
        var string = "1"
        val add = "1"
        var i = 0
        while (i < BENCHMARK_SIZE) {
            string += add
            i++
        }
        return string
    }
    
    @Benchmark
    fun stringConcatNullable(): String? {
        var string: String? = ""
        val add = "1"
        var i = 0
        while (i < BENCHMARK_SIZE) {
            string += add
            i++
        }
        return string
    }
    
    @Benchmark
    fun stringBuilderConcat(): String {
        val string = StringBuilder("")
        val add = "1"
        var i = 0
        while (i < BENCHMARK_SIZE) {
            string.append(add)
            i++
        }
        return string.toString()
    }
    
    @Benchmark
    fun stringBuilderConcatNullable(): String {
        var string: StringBuilder? = StringBuilder("")
        val add = "1"
        var i = 0
        while (i < BENCHMARK_SIZE) {
            string?.append(add)
            i++
        }
        return string.toString()
    }
    
    @Benchmark
    fun summarizeSplittedCsv(): Double {
        val fields = csv.split(",").toTypedArray()
        var sum = 0.0
        var i = 0
        val size = fields.size
        while (i < size) {
            sum += fields[i].toDouble()
            i++
        }
        return sum
    }

    @Benchmark
    fun iterateCharsCsv(): Int {
        var sum = 0
        for (i in 0 until BENCHMARK_SIZE) {
            sum += csv[i].code
        }
        return sum
    }

    @Benchmark
    fun subSequenceCsv(): Int {
        var sum = 0
        for (range in subSequenceRanges) {
            val subString = csv.subSequence(range.first, range.second)
            sum += subString[0].code
        }
        return sum
    }
}