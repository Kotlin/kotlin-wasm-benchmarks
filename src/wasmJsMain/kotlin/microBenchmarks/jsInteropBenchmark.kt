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

external interface IFace

@JsFun("(s) => {}")
private external fun stringInteropIn(s: String)

@JsFun("() => globalThis.benchmarkString")
private external fun stringInteropOut(): String

@JsFun("(i) => {}")
private external fun intInteropIn(i: Int)

@JsFun("() => 42")
private external fun intInteropOut(): Int

@JsFun("(o) => {}")
private external fun iFaceInteropIn(i: IFace)

@JsFun("() => globalThis.benchmarkObject")
private external fun iFaceInteropOut(): IFace

@JsFun("() => { }")
private external fun simpleInteropNoAdapters()

@JsFun("() => { globalThis.benchmarkObject = {}; globalThis.benchmarkString = 'WasmRulezzzFromJs'; }")
private external fun initializeGlobals()

@State(Scope.Benchmark)
class JsInteropBenchmark {
    private lateinit var externalIFace: IFace
    private lateinit var someString: String
    private var someInt: Int = 42

    @Setup
    fun setup() {
        initializeGlobals()
        externalIFace = iFaceInteropOut()
        someString = "WasmRulezzzzz"
    }

    @Benchmark
    fun simpleInterop() {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            simpleInteropNoAdapters()
            i++
        }
    }

    @Benchmark
    fun stringInteropIn() {
        val someString = someString
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            stringInteropIn(someString)
            i++
        }
    }

    @Benchmark
    fun stringInteropOut(blackhole: Blackhole) {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            blackhole.consume(stringInteropOut())
            i++
        }
    }

    @Benchmark
    fun intInteropIn() {
        val someInt = someInt
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            intInteropIn(someInt)
            i++
        }
    }

    @Benchmark
    fun intInteropOut(blackhole: Blackhole) {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            blackhole.consume(intInteropOut())
            i++
        }
    }

    @Benchmark
    fun externInteropIn() {
        val iFace = externalIFace
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            iFaceInteropIn(iFace)
            i++
        }
    }

    @Benchmark
    fun externInteropOut(blackhole: Blackhole) {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            blackhole.consume(iFaceInteropOut())
            i++
        }
    }
}