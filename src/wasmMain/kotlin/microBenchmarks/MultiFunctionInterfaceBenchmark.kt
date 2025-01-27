/*
 * Copyright 2025 JetBrains s.r.o.
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

private const val RUNS = 1_000_000

@State(Scope.Benchmark)
class MultiFunctionInterfaceBenchmark {
    interface IFunc :
        Function<Int>,
        Function1<Int, Int>,
        Function2<Int, Int, Int>,
        Function3<Int, Int, Int, Int>,
        Function4<Int, Int, Int, Int, Int>,
        Function5<Int, Int, Int, Int, Int, Int>

    @State(Scope.Benchmark)
    class IFuncClass : IFunc {
        override fun invoke(p1: Int): Int = p1

        override fun invoke(p1: Int, p2: Int): Int = p1 + p2

        override fun invoke(p1: Int, p2: Int, p3: Int): Int = p1 + p2 + p3

        override fun invoke(p1: Int, p2: Int, p3: Int, p4: Int): Int = p1 + p2 + p3 + p4

        override fun invoke(p1: Int, p2: Int, p3: Int, p4: Int, p5: Int): Int = p1 + p2 + p3 + p4 + p5
    }

    private lateinit var iFunc: IFunc

    @Setup
    fun setup() {
        iFunc = IFuncClass()
    }

    @Benchmark
    fun interfaceFunctionCall(): Int {
        val obj = iFunc
        var result = 0
        var i = 0
        while (i < RUNS) {
            result += obj(i)
            result += obj(i, i)
            result += obj(i, i, i)
            result += obj(i, i, i, i)
            result += obj(i, i, i, i, i)
            i++
        }
        return result
    }
}
