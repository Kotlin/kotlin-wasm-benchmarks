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
import kotlin.math.*

private const val TRIG_RANGE = 100.0

// Benchmarks for kotlin.math functions (on Kotlin/Wasm most of them are implemented via fdlibm). See KT-91749.
@State(Scope.Benchmark)
class MathBenchmark {
    // Arguments in [-TRIG_RANGE, TRIG_RANGE): require argument reduction (__ieee754_rem_pio2).
    private lateinit var trigArgs: DoubleArray
    // Arguments in [-PI/4, PI/4): handled by the trig kernels directly, without argument reduction.
    private lateinit var smallTrigArgs: DoubleArray
    // Arguments in [-1, 1).
    private lateinit var unitArgs: DoubleArray
    // Arguments in (0, 1e6].
    private lateinit var positiveArgs: DoubleArray
    // Arguments in [-50, 50).
    private lateinit var expArgs: DoubleArray
    // Arguments in [-5, 5).
    private lateinit var exponentArgs: DoubleArray
    private lateinit var floatTrigArgs: FloatArray

    @Setup
    fun setup() {
        val random = Random()
        trigArgs = DoubleArray(BENCHMARK_SIZE) { random.nextDouble(2 * TRIG_RANGE) - TRIG_RANGE }
        smallTrigArgs = DoubleArray(BENCHMARK_SIZE) { random.nextDouble(PI / 2) - PI / 4 }
        unitArgs = DoubleArray(BENCHMARK_SIZE) { random.nextDouble(2.0) - 1.0 }
        positiveArgs = DoubleArray(BENCHMARK_SIZE) { 1e6 - random.nextDouble(1e6) }
        expArgs = DoubleArray(BENCHMARK_SIZE) { random.nextDouble(100.0) - 50.0 }
        exponentArgs = DoubleArray(BENCHMARK_SIZE) { random.nextDouble(10.0) - 5.0 }
        floatTrigArgs = FloatArray(BENCHMARK_SIZE) { random.nextFloat(2 * TRIG_RANGE.toFloat()) - TRIG_RANGE.toFloat() }
    }

    @Benchmark
    fun cos(bh: Blackhole) {
        bh.consume(sumCos(trigArgs))
    }

    @Benchmark
    fun cosSmallArgument(bh: Blackhole) {
        bh.consume(sumCos(smallTrigArgs))
    }

    @Benchmark
    fun sin(bh: Blackhole) {
        bh.consume(sumSin(trigArgs))
    }

    @Benchmark
    fun sinSmallArgument(bh: Blackhole) {
        bh.consume(sumSin(smallTrigArgs))
    }

    @Benchmark
    fun tan(bh: Blackhole) {
        bh.consume(sumTan(trigArgs))
    }

    @Benchmark
    fun cosFloat(bh: Blackhole) {
        bh.consume(sumCosFloat(floatTrigArgs))
    }

    @Benchmark
    fun asin(bh: Blackhole) {
        bh.consume(sumAsin(unitArgs))
    }

    @Benchmark
    fun acos(bh: Blackhole) {
        bh.consume(sumAcos(unitArgs))
    }

    @Benchmark
    fun atan(bh: Blackhole) {
        bh.consume(sumAtan(trigArgs))
    }

    @Benchmark
    fun atan2(bh: Blackhole) {
        bh.consume(sumAtan2(trigArgs, unitArgs))
    }

    @Benchmark
    fun hypot(bh: Blackhole) {
        bh.consume(sumHypot(trigArgs, unitArgs))
    }

    @Benchmark
    fun exp(bh: Blackhole) {
        bh.consume(sumExp(expArgs))
    }

    @Benchmark
    fun expm1(bh: Blackhole) {
        bh.consume(sumExpm1(expArgs))
    }

    @Benchmark
    fun ln(bh: Blackhole) {
        bh.consume(sumLn(positiveArgs))
    }

    @Benchmark
    fun log10(bh: Blackhole) {
        bh.consume(sumLog10(positiveArgs))
    }

    @Benchmark
    fun ln1p(bh: Blackhole) {
        bh.consume(sumLn1p(positiveArgs))
    }

    @Benchmark
    fun pow(bh: Blackhole) {
        bh.consume(sumPow(positiveArgs, exponentArgs))
    }

    @Benchmark
    fun tanh(bh: Blackhole) {
        bh.consume(sumTanh(exponentArgs))
    }

    @Benchmark
    fun sqrt(bh: Blackhole) {
        bh.consume(sumSqrt(positiveArgs))
    }
}

private fun sumCos(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += cos(value)
    return sum
}

private fun sumSin(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += sin(value)
    return sum
}

private fun sumTan(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += tan(value)
    return sum
}

private fun sumCosFloat(values: FloatArray): Float {
    var sum = 0.0f
    for (value in values) sum += cos(value)
    return sum
}

private fun sumAsin(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += asin(value)
    return sum
}

private fun sumAcos(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += acos(value)
    return sum
}

private fun sumAtan(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += atan(value)
    return sum
}

private fun sumAtan2(ys: DoubleArray, xs: DoubleArray): Double {
    var sum = 0.0
    for (i in ys.indices) sum += atan2(ys[i], xs[i])
    return sum
}

private fun sumHypot(xs: DoubleArray, ys: DoubleArray): Double {
    var sum = 0.0
    for (i in xs.indices) sum += hypot(xs[i], ys[i])
    return sum
}

private fun sumExp(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += exp(value)
    return sum
}

private fun sumExpm1(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += expm1(value)
    return sum
}

private fun sumLn(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += ln(value)
    return sum
}

private fun sumLog10(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += log10(value)
    return sum
}

private fun sumLn1p(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += ln1p(value)
    return sum
}

private fun sumPow(bases: DoubleArray, exponents: DoubleArray): Double {
    var sum = 0.0
    for (i in bases.indices) sum += bases[i].pow(exponents[i])
    return sum
}

private fun sumTanh(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += tanh(value)
    return sum
}

private fun sumSqrt(values: DoubleArray): Double {
    var sum = 0.0
    for (value in values) sum += sqrt(value)
    return sum
}
