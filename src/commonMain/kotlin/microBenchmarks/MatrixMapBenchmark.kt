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

/*
 * This class emulates matrix behaviour using a hash map as its implementation
 */
private class KMatrix(val rows: Int, val columns: Int) {
    private val matrix: MutableMap<Pair<Int, Int>, Double> = HashMap()

    init {
        for (row in 0..< rows) {
            for (col in 0..< columns) {
                matrix[Pair(row, col)] = Random.nextDouble()
            }
        }
    }

    fun get(row: Int, col: Int): Double {
        return get(Pair(row, col))
    }

    fun get(pair: Pair<Int, Int>): Double {
        return matrix.getOrElse(pair, { 0.0 })
    }

    fun put(pair: Pair<Int, Int>, elem: Double) {
        matrix[pair] = elem
    }

    operator fun plusAssign(other: KMatrix) {
        for (entry in matrix.entries) {
            put(entry.key, entry.value + other.get(entry.key))
        }
    }
}

/*
 * This class tests hash map performance
 */
@State(Scope.Benchmark)
class MatrixMapBenchmark {

    private lateinit var a: KMatrix
    private lateinit var b: KMatrix

    @Setup
    fun setup() {
        var rows = BENCHMARK_SIZE
        var cols = 1
        while (rows > cols) {
            rows /= 2
            cols *= 2
        }
        a = KMatrix(rows, cols)
        b = KMatrix(rows, cols)
    }


    @Benchmark
    fun add(blackhole: Blackhole) {
        val result = KMatrix(rows = a.rows, columns = a.columns)

        repeat(BENCHMARK_SIZE) {
            result += a
            result += b
            result += a
            result += b
        }

        blackhole.consume(result)
    }
}