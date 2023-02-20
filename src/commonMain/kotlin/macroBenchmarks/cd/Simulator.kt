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

import macroBenchmarks.som.Vector
import kotlin.math.cos
import kotlin.math.sin

class Simulator(numAircraft: Int) {
    private val aircraft: Vector<CallSign> = Vector()

    init {
        require(numAircraft.rem(2) == 0) { "numAircraft should be divided on two" }
        for (i in 0 until numAircraft) {
            aircraft.append(CallSign(i))
        }
    }

    fun simulate(time: Double): Vector<Aircraft> {
        val frame = Vector<Aircraft>()
        for (i in 0 until aircraft.size step 2) {
            frame.append(
                Aircraft(
                    aircraft.at(i)!!,
                    Vector3D(time, cos(time) * 2 + i * 3, 10.0)
                )
            )
            frame.append(
                Aircraft(
                    aircraft.at(i + 1)!!,
                    Vector3D(time, sin(time) * 2 + i * 3, 10.0)
                )
            )

        }
        return frame
    }
}