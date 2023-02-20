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

import macroBenchmarks.cd.CollisionDetector
import macroBenchmarks.cd.Simulator
import kotlin.collections.List

class CD : MacroBenchmark() {
    private fun benchmark(numAircrafts: Int): Int {
        val numFrames = 200
        val simulator = Simulator(numAircrafts)
        val detector = CollisionDetector()
        var actualCollisions = 0
        for (i in 0 until numFrames) {
            val time = i / 10.0
            val collisions = detector.handleNewFrame(simulator.simulate(time))
            actualCollisions += collisions.size
        }
        return actualCollisions
    }

    override fun innerBenchmarkLoop(innerIterations: Int): Boolean {
        return verifyResult(benchmark(innerIterations), innerIterations)
    }

    private fun verifyResult(actualCollisions: Int, numAircrafts: Int): Boolean = when (numAircrafts) {
        1000 -> actualCollisions == 14484
        500 -> actualCollisions == 14484
        250 -> actualCollisions == 10830
        200 -> actualCollisions == 8655
        100 -> actualCollisions == 4305
        10 -> actualCollisions == 390
        2 -> actualCollisions == 42
        else -> false
    }

    override val defaultInnerIterations: List<Int> = listOf(2, 10, 100, 200, 250, 500, 1000)
}