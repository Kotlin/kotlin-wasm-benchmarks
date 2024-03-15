/*
 * Copyright 2024 JetBrains s.r.o.
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


class BoxingBenchmark {
    @Benchmark
    fun integerTypeBoxing(): Int {
        val size = BENCHMARK_SIZE
        var box: Int? = 42
        var unbox: Int = 24
        for (i in 0..size) {
            val value: Int = box!!
            box = unbox
            unbox = value
        }
        return box.hashCode() + unbox.hashCode()
    }

    @Benchmark
    fun booleanTypeBoxing(): Int {
        val size = BENCHMARK_SIZE
        var box: Boolean? = true
        var unbox: Boolean = false
        for (i in 0..size) {
            val value: Boolean = box!!
            box = unbox
            unbox = value
        }
        return box.hashCode() + unbox.hashCode()
    }

    @Benchmark
    fun referenceTypeVarClosure(): Int {
        val size = BENCHMARK_SIZE * 10
        var varBox1: Any = Any()
        var varBox2: Any = Any()
        val closure = {
            val value = varBox1
            varBox1 = varBox2
            varBox2 = value
        }

        for (i in 0..size) {
            closure()
        }

        return varBox1.hashCode() + varBox2.hashCode()
    }

    @Benchmark
    fun integerTypeVarClosure(): Int {
        val size = BENCHMARK_SIZE * 10
        var varBox1: Int = 42
        var varBox2: Int = 24
        val closure = {
            val value = varBox1
            varBox1 = varBox2
            varBox2 = value
        }

        for (i in 0..size) {
            closure()
        }

        return varBox1.hashCode() + varBox2.hashCode()
    }
}