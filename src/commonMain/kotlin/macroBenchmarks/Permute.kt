/* This code is based on the SOM class library.
 *
 * Copyright (c) 2001-2016 see AUTHORS.md file
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

class Permute : MacroBenchmark() {
    private var count = 0
    override fun benchmark(): Any {
        count = 0
        permute(6, IntArray(6))
        return count
    }

    override fun verifyResult(result: Any): Boolean =
        result as Int == 8660

    private fun permute(n: Int, v: IntArray) {
        count++
        if (n != 0) {
            val n1 = n - 1
            permute(n1, v)
            for (i in n1 downTo 0) {
                swap(n1, i, v)
                permute(n1, v)
                swap(n1, i, v)
            }
        }
    }

    private fun swap(i: Int, j: Int, v: IntArray) {
        val tmp = v[i]
        v[i] = v[j]
        v[j] = tmp
    }
}