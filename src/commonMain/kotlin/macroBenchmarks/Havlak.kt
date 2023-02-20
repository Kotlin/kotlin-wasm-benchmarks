/*
 * Copyright (c) 2001-2016 Stefan Marr
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the 'Software'), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package macroBenchmarks

import macroBenchmarks.havlak.LoopTesterApp
import kotlin.collections.List

class Havlak : MacroBenchmark() {
    override fun innerBenchmarkLoop(innerIterations: Int): Boolean {
        return verifyResult(
            result = LoopTesterApp().main(
                numDummyLoops = innerIterations,
                findLoopIterations = 50,
                parLoops = 10 /* was 100 */,
                pparLoops = 10,
                ppparLoops = 5
            ),
            innerIterations = innerIterations
        )
    }

    fun verifyResult(result: Any, innerIterations: Int): Boolean {
        val r = result as IntArray
        return when (innerIterations) {
            15000 -> r[0] == 46602 && r[1] == 5213
            1500 -> r[0] == 6102 && r[1] == 5213
            150 -> r[0] == 2052 && r[1] == 5213
            15 -> r[0] == 1647 && r[1] == 5213
            1 -> r[0] == 1605 && r[1] == 5213
            else -> false
        }
    }

    override val defaultInnerIterations: List<Int> = listOf(1, 15, 150, 1500, 15000)
}