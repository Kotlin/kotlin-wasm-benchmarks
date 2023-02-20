package macroBenchmarks

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

class Queens : MacroBenchmark() {
    override fun benchmark(): Any {
        var result = true
        for (i in 0..9) {
            result = result && QueensImplementation().queens()
        }
        return result
    }

    override fun verifyResult(result: Any): Boolean =
        result as Boolean

    class QueensImplementation {
        private val freeRows = BooleanArray(8)
        private val freeMaxs = BooleanArray(16)
        private val freeMins = BooleanArray(16)
        private val queenRows = IntArray(8)

        fun queens(): Boolean {
            freeRows.fill(true)
            freeRows.fill(true)
            freeMaxs.fill(true)
            freeMins.fill(true)
            queenRows.fill(-1)
            return placeQueen(0)
        }

        private fun placeQueen(c: Int): Boolean {
            for (r in 0..7) {
                if (getRowColumn(r, c)) {
                    queenRows[r] = c
                    setRowColumn(r, c, false)
                    if (c == 7) {
                        return true
                    }
                    if (placeQueen(c + 1)) {
                        return true
                    }
                    setRowColumn(r, c, true)
                }
            }
            return false
        }

        private fun getRowColumn(r: Int, c: Int): Boolean =
            freeRows[r] && freeMaxs[c + r] && freeMins[c - r + 7]

        private fun setRowColumn(r: Int, c: Int, v: Boolean) {
            freeRows[r] = v
            freeMaxs[c + r] = v
            freeMins[c - r + 7] = v
        }
    }
}