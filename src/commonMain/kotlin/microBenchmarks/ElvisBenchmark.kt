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

private class Composite(val x : Int, val y : Composite?)

private fun check(a : Composite?) : Int {
    return a?.y?.x ?: (a?.x ?: 3)
}

private class SomeValue(var value: Int)

@State(Scope.Benchmark)
class ElvisBenchmark {
    private lateinit var array : Array<SomeValue?>

    @Setup
    fun setup() {
        array = Array(BENCHMARK_SIZE) {
            if (Random.nextInt(BENCHMARK_SIZE) < BENCHMARK_SIZE / 10) null else SomeValue(Random.nextInt())
        }
    }

    @Benchmark
    fun testElvis(blackhole: Blackhole) {
        var result = 0
        for (obj in array) {
            result += obj?.value ?: 0
        }
        blackhole.consume(result)
    }

    @Benchmark
    fun testCompositeElvis(blackhole: Blackhole) {
        var result = 0
        repeat(BENCHMARK_SIZE) {
            result += check(Composite(Random.nextInt(), Composite(Random.nextInt(), null)))
        }
        blackhole.consume(result)
    }
}
