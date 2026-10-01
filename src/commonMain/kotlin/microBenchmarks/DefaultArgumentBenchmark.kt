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

/*
 * Created by Mikhail.Glukhikh on 10/03/2015.
 *
 * Tests performance for function calls with default parameters
 */

private fun sumTwo(first: Int, second: Int = 0): Int {
    return first + second
}

private fun sumFour(first: Int, second: Int = 0, third: Int = 1, fourth: Int = third): Int {
    return first + second + third + fourth
}

private fun sumEight(first: Int, second: Int = 0, third: Int = 1, fourth: Int = third,
             fifth: Int = fourth, sixth: Int = fifth, seventh: Int = second, eighth: Int = seventh): Int {
    return first + second + third + fourth + fifth + sixth + seventh + eighth
}

@State(Scope.Benchmark)
class DefaultArgumentBenchmark {
    private var arg = 0

    @Setup
    fun setup() {
        arg = Random.nextInt()
    }

    @Benchmark
    fun testOneOfTwo(blackhole: Blackhole) {
        val result = sumTwo(arg)
        blackhole.consume(result)
    }

    @Benchmark
    fun testTwoOfTwo(blackhole: Blackhole) {
        val result = sumTwo(arg, arg)
        blackhole.consume(result)
    }
    
    @Benchmark
    fun testOneOfFour(blackhole: Blackhole) {
        val result = sumFour(arg)
        blackhole.consume(result)
    }

    @Benchmark
    fun testFourOfFour(blackhole: Blackhole) {
        val result = sumFour(arg, arg, arg, arg)
        blackhole.consume(result)
    }

    @Benchmark
    fun testOneOfEight(blackhole: Blackhole) {
        val result = sumEight(arg)
        blackhole.consume(result)
    }

    @Benchmark
    fun testEightOfEight(blackhole: Blackhole) {
        val result = sumEight(arg, arg, arg, arg, arg, arg, arg, arg)
        blackhole.consume(result)
    }
}
