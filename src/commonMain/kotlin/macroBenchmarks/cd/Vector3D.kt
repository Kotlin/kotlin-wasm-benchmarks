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

import kotlin.math.sqrt

class Vector3D(val x: Double, val y: Double, val z: Double) {
    operator fun plus(other: Vector3D): Vector3D =
        Vector3D(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Vector3D): Vector3D =
        Vector3D(x - other.x, y - other.y, z - other.z)

    fun dot(other: Vector3D): Double =
        x * other.x + y * other.y + z * other.z

    fun squaredMagnitude(): Double =
        dot(this)

    fun magnitude(): Double =
        sqrt(squaredMagnitude())

    operator fun times(amount: Double): Vector3D =
        Vector3D(x * amount, y * amount, z * amount)
}