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

var globalAddendum = 0

@State(Scope.Benchmark)
class LambdaBenchmark {
    private inline fun <T> runLambda(x: () -> T): T = x()
    private fun <T> runLambdaNoInline(x: () -> T): T = x()

    @Setup
    fun setup() {
        globalAddendum = Random.nextInt(20)
    }

    @Benchmark
    fun noncapturingLambda(): Int {
        var x: Int = 0
        var i = 0
        while (i < BENCHMARK_SIZE) {
            x += runLambda { globalAddendum }
            i++
        }
        return x
    }

    @Benchmark
    fun noncapturingLambdaNoInline(): Int {
        var x: Int = 0
        var i = 0
        while (i < BENCHMARK_SIZE) {
            x += runLambdaNoInline { globalAddendum }
            i++
        }
        return x
    }

    @Benchmark
    fun capturingLambda(): Int {
        val addendum = globalAddendum + 1
        var x: Int = 0
        var i = 0
        while (i < BENCHMARK_SIZE) {
            x += runLambda { addendum }
            i++
        }
        return x
    }

    @Benchmark
    fun capturingLambdaNoInline(): Int {
        val addendum = globalAddendum + 1
        var x: Int = 0
        var i = 0
        while (i < BENCHMARK_SIZE) {
            x += runLambdaNoInline { addendum }
            i++
        }
        return x
    }

    @Benchmark
    fun mutatingLambda(): Int {
        var x: Int = 0
        var i = 0
        while (i < BENCHMARK_SIZE) {
            runLambda { x += globalAddendum }
            i++
        }
        return x
    }

    @Benchmark
    fun mutatingLambdaNoInline(): Int {
        var x: Int = 0
        var i = 0
        while (i < BENCHMARK_SIZE) {
            runLambdaNoInline { x += globalAddendum }
            i++
        }
        return x
    }

    @Benchmark
    fun methodReference(): Int {
        var x: Int = 0
        var i = 0
        while (i < BENCHMARK_SIZE) {
            x += runLambda(::referenced)
            i++
        }
        return x
    }

    @Benchmark
    fun methodReferenceNoInline(): Int {
        var x: Int = 0
        var i = 0
        while (i < BENCHMARK_SIZE) {
            x += runLambdaNoInline(::referenced)
            i++
        }
        return x
    }
}

private fun referenced(): Int {
    return globalAddendum
}
