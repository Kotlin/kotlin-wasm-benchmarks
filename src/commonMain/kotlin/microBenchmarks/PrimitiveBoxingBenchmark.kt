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

/*
 * Benchmarks for primitive boxing on Kotlin/Wasm (KT-66089 small boxes cache, KT-91749, KT-86965, KT-86967).
 *
 * "Redundant" benchmarks contain boxing which the compiler could avoid entirely
 * (default arguments, safe call chains, value class equality).
 * "Required" benchmarks contain boxing which semantically has to happen (Any, generics, nullable types)
 * and measure the cost of a single boxing operation for values inside and outside the small boxes cache range.
 */

private const val SMALL_VALUES_RANGE = 256 // -128..127, covered by the boxes cache
private const val LARGE_VALUE_OFFSET = 100_000 // outside of the boxes cache

private value class Meters(val value: Int)

private class IntHolder(val value: Int)

// Mirrors kotlin.math.fdlibm.doubleSetWord: primitive parameters with non-constant defaults.
private fun intDefaults(x: Int, hi: Int = x ushr 16, lo: Int = x and 0xFFFF): Int =
    (hi shl 16) or (lo xor 0x5A5A)

private fun mixedDefaults(x: Int, l: Long = x.toLong() * 3, c: Char = 'k', b: Boolean = x > 0): Long =
    if (b) l + c.code else l - c.code

private fun valueClassDefaults(m: Meters, factor: Meters = Meters(3)): Meters =
    Meters(m.value * factor.value)

private fun nullableIntChain(holder: IntHolder?): Int? =
    holder?.value?.inc()?.dec()

private fun intChainWithElvis(holder: IntHolder?): Int =
    holder?.value?.inc()?.dec() ?: 0

private fun positiveOrNull(x: Int): Int? = if (x >= 0) x else null

private fun <T> identity(x: T): T = x

private fun applyIntFunction(f: (Int) -> Int, x: Int): Int = f(x)

@State(Scope.Benchmark)
class PrimitiveBoxingBenchmark {
    private lateinit var smallInts: IntArray
    private lateinit var largeInts: IntArray
private lateinit var holders: Array<IntHolder?>

    @Setup
    fun setup() {
        val random = Random()
        smallInts = IntArray(BENCHMARK_SIZE) { random.nextInt(SMALL_VALUES_RANGE) - SMALL_VALUES_RANGE / 2 }
        largeInts = IntArray(BENCHMARK_SIZE) { random.nextInt(SMALL_VALUES_RANGE) + LARGE_VALUE_OFFSET }
        holders = Array(BENCHMARK_SIZE) {
            val value = random.nextInt(SMALL_VALUES_RANGE)
            if (value % 10 == 0) null else IntHolder(value)
        }
    }

    // ---------------- Redundant boxing ----------------

    @Benchmark
    fun defaultArgumentsInt(bh: Blackhole) {
        val values = largeInts
        var sum = 0
        for (v in values) sum += intDefaults(v, lo = v)
        bh.consume(sum)
    }

    @Benchmark
    fun defaultArgumentsMixed(bh: Blackhole) {
        val values = smallInts
        var sum = 0L
        for (v in values) sum += mixedDefaults(v, c = 'z')
        bh.consume(sum)
    }

    @Benchmark
    fun defaultArgumentsValueClass(bh: Blackhole) {
        val values = largeInts
        var sum = 0
        for (v in values) sum += valueClassDefaults(Meters(v)).value
        bh.consume(sum)
    }

    @Benchmark
    fun valueClassEquality(bh: Blackhole) {
        val values = largeInts
        var count = 0
        var previous = Meters(0)
        for (v in values) {
            val current = Meters(v)
            if (current == previous) count++
            previous = current
        }
        bh.consume(count)
    }

    @Benchmark
    fun safeCallChainWithElvis(bh: Blackhole) {
        val holders = holders
        var sum = 0
        for (h in holders) sum += intChainWithElvis(h)
        bh.consume(sum)
    }

    // ---------------- Required boxing ----------------

    @Benchmark
    fun safeCallChainNullable(bh: Blackhole) {
        val holders = holders
        var sum = 0
        for (h in holders) sum += nullableIntChain(h) ?: 0
        bh.consume(sum)
    }

    @Benchmark
    fun nullableIntSmall(bh: Blackhole) {
        bh.consume(sumPositiveOrNull(smallInts))
    }

    @Benchmark
    fun nullableIntLarge(bh: Blackhole) {
        bh.consume(sumPositiveOrNull(largeInts))
    }

    @Benchmark
    fun genericIntSmall(bh: Blackhole) {
        bh.consume(sumGenericInt(smallInts))
    }

    @Benchmark
    fun genericIntLarge(bh: Blackhole) {
        bh.consume(sumGenericInt(largeInts))
    }

    @Benchmark
    fun anyArrayIntSmall(bh: Blackhole) {
        bh.consume(sumThroughAnyArray(smallInts))
    }

    @Benchmark
    fun anyArrayIntLarge(bh: Blackhole) {
        bh.consume(sumThroughAnyArray(largeInts))
    }

    @Benchmark
    fun genericLong(bh: Blackhole) {
        val values = smallInts
        var sum = 0L
        for (v in values) sum += identity(v.toLong())
        bh.consume(sum)
    }

    @Benchmark
    fun genericChar(bh: Blackhole) {
        val values = smallInts
        var sum = 0
        for (v in values) sum += identity((v and 0x7F).toChar()).code
        bh.consume(sum)
    }

    @Benchmark
    fun genericByte(bh: Blackhole) {
        val values = smallInts
        var sum = 0
        for (v in values) sum += identity(v.toByte())
        bh.consume(sum)
    }

    @Benchmark
    fun genericShort(bh: Blackhole) {
        val values = smallInts
        var sum = 0
        for (v in values) sum += identity(v.toShort())
        bh.consume(sum)
    }

    @Benchmark
    fun genericBoolean(bh: Blackhole) {
        val values = smallInts
        var count = 0
        for (v in values) if (identity(v > 0)) count++
        bh.consume(count)
    }

    // Not cached: control benchmark.
    @Benchmark
    fun genericDouble(bh: Blackhole) {
        val values = smallInts
        var sum = 0.0
        for (v in values) sum += identity(v.toDouble())
        bh.consume(sum)
    }

    @Benchmark
    fun listOfIntSmall(bh: Blackhole) {
        bh.consume(sumThroughList(smallInts))
    }

    @Benchmark
    fun listOfIntLarge(bh: Blackhole) {
        bh.consume(sumThroughList(largeInts))
    }

    // Inline Iterable<T> function on a range: the loop is lowered to an induction variable, which is boxed as T
    // on every iteration (same pattern as `(0 until n).any { ... }` in stdlib regex BitSet.intersects).
    @Benchmark
    fun rangeInlineHigherOrderFunction(bh: Blackhole) {
        bh.consume(countPositiveByIndex(smallInts))
    }

    @Benchmark
    fun functionTypeInt(bh: Blackhole) {
        val values = smallInts
        val f: (Int) -> Int = { it * 3 + 1 }
        var sum = 0
        for (v in values) sum += applyIntFunction(f, v)
        bh.consume(sum)
    }
}

private fun sumPositiveOrNull(values: IntArray): Int {
    var sum = 0
    for (v in values) sum += positiveOrNull(v) ?: 1
    return sum
}

private fun sumGenericInt(values: IntArray): Int {
    var sum = 0
    for (v in values) sum += identity(v)
    return sum
}

private fun sumThroughAnyArray(values: IntArray): Int {
    val boxes = arrayOfNulls<Any>(values.size)
    for (i in values.indices) boxes[i] = values[i]
    var sum = 0
    for (box in boxes) sum += box as Int
    return sum
}

private fun countPositiveByIndex(values: IntArray): Int =
    (0 until values.size).count { values[it] > 0 }

private fun sumThroughList(values: IntArray): Int {
    val list = ArrayList<Int>(values.size)
    for (v in values) list.add(v)
    var sum = 0
    for (v in list) sum += v
    return sum
}
