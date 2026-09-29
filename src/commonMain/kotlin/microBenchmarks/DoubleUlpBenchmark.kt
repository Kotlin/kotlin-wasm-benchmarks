/*
 * Copyright 2026 JetBrains s.r.o.
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
import kotlin.math.ulp

@State(Scope.Benchmark)
class DoubleUlpBenchmark {
    private lateinit var normalValues: DoubleArray
    private lateinit var specialValues: DoubleArray
    private lateinit var mixedValues: DoubleArray

    @Setup
    fun setup() {
        normalValues = DoubleArray(BENCHMARK_SIZE) { index ->
            val sign = if ((index % 2) == 0) 0L else Long.MIN_VALUE
            // Visit every normal Double exponent once per 2046 indices.
            val exponent = (1 + index * 73 % 2046).toLong() shl 52
            // An odd multiplier gives distinct 52-bit fractions for the first 2^52 indices.
            val fraction = (index.toLong() * 0x9E37_79B9_7F4A_7C15uL.toLong()) and 0x000f_ffff_ffff_ffffL
            Double.fromBits(sign or exponent or fraction)
        }

        specialValues = doubleArrayOf(
            0.0, -0.0, Double.MIN_VALUE, -Double.MIN_VALUE,
            Double.MAX_VALUE, -Double.MAX_VALUE, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY, Double.NaN
        )

        mixedValues = DoubleArray(BENCHMARK_SIZE) { index ->
            val specialValuePeriod = 8
            if ((index % specialValuePeriod) == 0) {
                specialValues[(index / specialValuePeriod) % specialValues.size]
            } else {
                normalValues[index]
            }
        }
    }

    @Benchmark
    fun normal(bh: Blackhole) {
        bh.consume(ulps(normalValues))
    }

    @Benchmark
    fun mixed(bh: Blackhole) {
        bh.consume(ulps(mixedValues))
    }
}

private fun ulps(values: DoubleArray) : Long {
    var sum = 0L
    for (value in values) {
        sum += value.ulp.toRawBits()
    }
    return sum
}
