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

package macroBenchmarks.cd

class Vector2D(val x: Double, val y: Double) : Comparable<Vector2D> {
    operator fun plus(other: Vector2D): Vector2D =
        Vector2D(x + other.x, y + other.y)

    operator fun minus(other: Vector2D): Vector2D =
        Vector2D(x - other.x, y - other.y)

    override fun compareTo(other: Vector2D): Int {
        val result = compareNumbers(x, other.x)
        return if (result != 0) result else compareNumbers(y, other.y)
    }

    companion object {
        private fun compareNumbers(a: Double, b: Double): Int = when {
            a == b -> 0
            a < b -> -1
            a > b -> 1
            // We say that NaN is smaller than non-NaN.
            else -> if (a == a) 1 else -1
        }
    }
}