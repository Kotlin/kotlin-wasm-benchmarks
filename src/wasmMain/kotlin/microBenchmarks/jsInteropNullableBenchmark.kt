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

@JsFun("(s) => {}")
private external fun stringInteropIn(s: String?)

@JsFun("(s, s2) => {}")
private external fun stringInteropIn2(s: String?, s2: String?)

@JsFun("() => globalThis.benchmarkString")
private external fun stringInteropOutNotNull(): String?

@JsFun("() => null")
private external fun stringInteropOutNull(): String?

@JsFun("(i) => {}")
private external fun intInteropIn(i: Int?)

@JsFun("(i, i2) => {}")
private external fun intInteropIn2(i: Int?, i2: Int?)

@JsFun("() => 42")
private external fun intInteropOutNotNull(): Int?

@JsFun("() => null")
private external fun intInteropOutNull(): Int?

@JsFun("(o) => {}")
private external fun iFaceInteropIn(i: IFace?)

@JsFun("(o, o2) => {}")
private external fun iFaceInteropIn2(o: IFace?, o2: IFace?)

@JsFun("() => { return globalThis.benchmarkObject }")
private external fun iFaceInteropOutNotNull(): IFace?

@JsFun("() => null")
private external fun iFaceInteropOutNull(): IFace?

@JsFun("() => { globalThis.benchmarkObject = {}; globalThis.benchmarkString = 'WasmRulezzzFromJs'; }")
private external fun initializeGlobals()

@State(Scope.Benchmark)
class JsInteropNullableBenchmark {
    private var externalIFaceNotNull: IFace? = null
    private var externalIFaceNotNull2: IFace? = null
    private var someStringNotNull: String? = null
    private var someStringNotNull2: String? = null
    private var externalIFaceNull: IFace? = null
    private var someStringNull: String? = null
    private var someIntNotNull: Int? = null
    private var someIntNotNull2: Int? = null
    private var someIntNull: Int? = null

    @Setup
    fun setup() {
        initializeGlobals()
        externalIFaceNotNull = iFaceInteropOutNotNull()
        externalIFaceNotNull2 = iFaceInteropOutNotNull()
        externalIFaceNull = iFaceInteropOutNull()
        someStringNotNull = stringInteropOutNotNull()
        someStringNotNull2 = stringInteropOutNotNull()
        someStringNull = stringInteropOutNull()
        someIntNotNull = intInteropOutNotNull()
        someIntNotNull2 = intInteropOutNotNull()
        someIntNull = intInteropOutNull()
    }

    @Benchmark
    fun stringInteropInNotNull() {
        val str = someStringNotNull
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            stringInteropIn(str)
            i++
        }
    }

    @Benchmark
    fun stringInteropInNotNull2Params() {
        val str = someStringNotNull
        val str2 = someStringNotNull2
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            stringInteropIn2(str, str2)
            i++
        }
    }

    @Benchmark
    fun stringInteropInNull() {
        val str = someStringNull
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            stringInteropIn(str)
            i++
        }
    }

    @Benchmark
    fun stringInteropOutNotNull(blackhole: Blackhole) {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            blackhole.consume(stringInteropOutNotNull())
            i++
        }
    }

    @Benchmark
    fun stringInteropOutNull(blackhole: Blackhole) {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            blackhole.consume(stringInteropOutNull())
            i++
        }
    }

    @Benchmark
    fun intInteropInNotNull() {
        val someInt = someIntNotNull
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            intInteropIn(someInt)
            i++
        }
    }

    @Benchmark
    fun intInteropInNotNull2Params() {
        val someInt = someIntNotNull
        val someInt2 = someIntNotNull2
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            intInteropIn2(someInt, someInt2)
            i++
        }
    }

    @Benchmark
    fun intInteropInNull() {
        val someInt = someIntNull
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            intInteropIn(someInt)
            i++
        }
    }

    @Benchmark
    fun intInteropOutNotNull(blackhole: Blackhole) {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            blackhole.consume(intInteropOutNotNull())
            i++
        }
    }

    @Benchmark
    fun intInteropOutNull(blackhole: Blackhole) {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            blackhole.consume(intInteropOutNull())
            i++
        }
    }

    @Benchmark
    fun externInteropInNotNull() {
        val iFace = externalIFaceNotNull
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            iFaceInteropIn(iFace)
            i++
        }
    }

    @Benchmark
    fun externInteropInNotNull2Params() {
        val iFace = externalIFaceNotNull
        val iFace2 = externalIFaceNotNull2
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            iFaceInteropIn2(iFace, iFace2)
            i++
        }
    }

    @Benchmark
    fun externInteropInNull() {
        val iFace = externalIFaceNull
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            iFaceInteropIn(iFace)
            i++
        }
    }

    @Benchmark
    fun externInteropOutNotNull(blackhole: Blackhole) {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            blackhole.consume(iFaceInteropOutNotNull())
            i++
        }
    }

    @Benchmark
    fun externInteropOutNull(blackhole: Blackhole) {
        var i = 0
        val size = BENCHMARK_SIZE
        while (i < size) {
            blackhole.consume(iFaceInteropOutNull())
            i++
        }
    }
}