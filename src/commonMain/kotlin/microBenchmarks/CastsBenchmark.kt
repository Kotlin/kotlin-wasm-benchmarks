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

private const val RUNS = 2_000_000 /* TODO: JS perf problem */ / 100

@State(Scope.Benchmark)
class CastsBenchmark {
    interface I0
    open class C0: I0
    interface I1
    open class C1: C0(), I1
    interface I2: I0
    open class C2: C1(), I2
    interface I3: I1
    open class C3: C2(), I3
    interface I4: I0, I2
    open class C4: C3(), I4
    interface I5: I3
    open class C5: C4(), I5
    interface I6: I0, I2, I4
    open class C6: C5(), I6
    interface I9: I0, I2, I4
    open class C9: C5(), I9, I1
    interface I7: I1
    open class C7: C3(), I7
    interface I8: I0, I1
    open class C8: C3(), I8

    private fun foo_class(c: Any, x: Int, i: Int): Int {
        @Suppress("NAME_SHADOWING") var x = x
        if (c is C0) x += i
        if (c is C1) x = x xor i
        if (c is C2) x += i
        if (c is C3) x = x xor i
        if (c is C4) x += i
        if (c is C5) x = x xor i
        if (c is C6) x += i
        if (c is C7) x = x xor i
        if (c is C8) x += i
        if (c is C9) x = x xor i
        return x
    }

    private fun foo_iface(c: Any, x: Int, i: Int): Int {
        @Suppress("NAME_SHADOWING") var x = x
        if (c is I0) x += i
        if (c is I1) x = x xor i
        if (c is I2) x += i
        if (c is I3) x = x xor i
        if (c is I4) x += i
        if (c is I5) x = x xor i
        if (c is I6) x += i
        if (c is I7) x = x xor i
        if (c is I8) x += i
        if (c is I9) x = x xor i
        return x
    }

    private lateinit var c0: Any
    private lateinit var c1: Any
    private lateinit var c2: Any
    private lateinit var c3: Any
    private lateinit var c4: Any
    private lateinit var c5: Any
    private lateinit var c6: Any
    private lateinit var c7: Any
    private lateinit var c8: Any
    private lateinit var c9: Any

    @Setup
    fun setup() {
        c0 = C0()
        c1 = C1()
        c2 = C2()
        c3 = C3()
        c4 = C4()
        c5 = C5()
        c6 = C6()
        c7 = C7()
        c8 = C8()
        c9 = C9()
    }

    @Benchmark
    fun classCast(): Int {
        val c0 = c0
        val c1 = c1
        val c2 = c2
        val c3 = c3
        val c4 = c4
        val c5 = c5
        val c6 = c6
        val c7 = c7
        val c8 = c8
        val c9 = c9

        var x = 0
        var i = 0
        while (i < RUNS) {
            x += foo_class(c0, x, i)
            x += foo_class(c1, x, i)
            x += foo_class(c2, x, i)
            x += foo_class(c3, x, i)
            x += foo_class(c4, x, i)
            x += foo_class(c5, x, i)
            x += foo_class(c6, x, i)
            x += foo_class(c7, x, i)
            x += foo_class(c8, x, i)
            x += foo_class(c9, x, i)
            i++
        }
        return x
    }

    @Benchmark
    fun interfaceCast(): Int {
        val c0 = c0
        val c1 = c1
        val c2 = c2
        val c3 = c3
        val c4 = c4
        val c5 = c5
        val c6 = c6
        val c7 = c7
        val c8 = c8
        val c9 = c9

        var x = 0
        var i = 0
        while (i < RUNS) {
            x += foo_iface(c0, x, i)
            x += foo_iface(c1, x, i)
            x += foo_iface(c2, x, i)
            x += foo_iface(c3, x, i)
            x += foo_iface(c4, x, i)
            x += foo_iface(c5, x, i)
            x += foo_iface(c6, x, i)
            x += foo_iface(c7, x, i)
            x += foo_iface(c8, x, i)
            x += foo_iface(c9, x, i)
            i++
        }
        return x
    }
}