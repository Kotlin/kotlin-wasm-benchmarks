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

class CollisionDetector {
    private val state: RedBlackTree<CallSign, Vector3D> = RedBlackTree()

    fun handleNewFrame(frame: Vector<Aircraft>): Vector<Collision> {
        val motions = Vector<Motion>()
        val seen = RedBlackTree<CallSign, Boolean>()
        frame.forEach { aircraft: Aircraft ->
            var oldPosition = state.put(aircraft.callsign, aircraft.position)
            val newPosition = aircraft.position
            seen.put(aircraft.callsign, true)
            if (oldPosition == null) {
                // Treat newly introduced aircraft as if they were stationary.
                oldPosition = newPosition
            }
            motions.append(Motion(aircraft.callsign, oldPosition, newPosition))
        }

        // Remove aircraft that are no longer present.
        val toRemove = Vector<CallSign>()
        state.forEach { e ->
            if (seen[e.key] != true) {
                toRemove.append(e.key)
            }
        }
        toRemove.forEach { e: CallSign -> state.remove(e) }
        val allReduced = reduceCollisionSet(motions)
        val collisions = Vector<Collision>()
        allReduced.forEach { reduced: Vector<Motion> ->
            for (i in 0 until reduced.size) {
                val motion1 = reduced.at(i)
                for (j in i + 1 until reduced.size) {
                    val motion2 = reduced.at(j)!!
                    val collision = motion1!!.findIntersection(motion2)
                    if (collision != null) {
                        collisions.append(Collision(motion1.callsign, motion2.callsign, collision))
                    }
                }
            }
        }
        return collisions
    }

    companion object {
        private fun isInVoxel(voxel: Vector2D, motion: Motion): Boolean {
            if (voxel.x > Constants.MAX_X || voxel.x < Constants.MIN_X || voxel.y > Constants.MAX_Y || voxel.y < Constants.MIN_Y) {
                return false
            }
            val init = motion.posOne
            val fin = motion.posTwo
            val vS = Constants.GOOD_VOXEL_SIZE
            val r = Constants.PROXIMITY_RADIUS / 2.0
            val vX = voxel.x
            val x0 = init.x
            val xv = fin.x - init.x
            val vY = voxel.y
            val y0 = init.y
            val yv = fin.y - init.y
            var lowX = (vX - r - x0) / xv
            var highX = (vX + vS + r - x0) / xv
            if (xv < 0.0) {
                val tmp = lowX
                lowX = highX
                highX = tmp
            }
            var lowY = (vY - r - y0) / yv
            var highY = (vY + vS + r - y0) / yv
            if (yv < 0.0) {
                val tmp = lowY
                lowY = highY
                highY = tmp
            }
            return (xv == 0.0 && vX <= x0 + r && x0 - r <= vX + vS /* no motion in x */ ||
                    lowX <= 1.0 && 1.0 <= highX || lowX <= 0.0 && 0.0 <= highX ||
                    0.0 <= lowX && highX <= 1.0) &&
                    (yv == 0.0 && vY <= y0 + r && y0 - r <= vY + vS /* no motion in y */ ||
                            lowY <= 1.0 && 1.0 <= highY || lowY <= 0.0 && 0.0 <= highY ||
                            0.0 <= lowY && highY <= 1.0) &&
                    (xv == 0.0 || yv == 0.0 ||  /* no motion in x or y or both */
                            highX in lowY..highY ||
                            lowX in lowY..highY ||
                            lowX <= lowY && highY <= highX)
        }

        private val horizontal = Vector2D(Constants.GOOD_VOXEL_SIZE, 0.0)
        private val vertical = Vector2D(0.0, Constants.GOOD_VOXEL_SIZE)

        private fun putIntoMap(
            voxelMap: RedBlackTree<Vector2D, Vector<Motion>>,
            voxel: Vector2D, motion: Motion
        ) {
            var array = voxelMap[voxel]
            if (array == null) {
                array = Vector()
                voxelMap.put(voxel, array)
            }
            array.append(motion)
        }

        private fun recurse(
            voxelMap: RedBlackTree<Vector2D, Vector<Motion>>,
            seen: RedBlackTree<Vector2D, Boolean>,
            nextVoxel: Vector2D, motion: Motion
        ) {
            if (!isInVoxel(nextVoxel, motion)) {
                return
            }
            if (seen.put(nextVoxel, true) == true) {
                return
            }
            putIntoMap(voxelMap, nextVoxel, motion)
            recurse(voxelMap, seen, nextVoxel.minus(horizontal), motion)
            recurse(voxelMap, seen, nextVoxel.plus(horizontal), motion)
            recurse(voxelMap, seen, nextVoxel.minus(vertical), motion)
            recurse(voxelMap, seen, nextVoxel.plus(vertical), motion)
            recurse(voxelMap, seen, nextVoxel.minus(horizontal).minus(vertical), motion)
            recurse(voxelMap, seen, nextVoxel.minus(horizontal).plus(vertical), motion)
            recurse(voxelMap, seen, nextVoxel.plus(horizontal).minus(vertical), motion)
            recurse(voxelMap, seen, nextVoxel.plus(horizontal).plus(vertical), motion)
        }

        private fun reduceCollisionSet(motions: Vector<Motion>): Vector<Vector<Motion>> {
            val voxelMap = RedBlackTree<Vector2D, Vector<Motion>>()
            motions.forEach { motion: Motion -> drawMotionOnVoxelMap(voxelMap, motion) }
            val result = Vector<Vector<Motion>>()
            voxelMap.forEach { e ->
                if (e.value.size > 1) {
                    result.append(e.value)
                }
            }
            return result
        }

        private fun voxelHash(position: Vector3D): Vector2D {
            val xDiv = (position.x / Constants.GOOD_VOXEL_SIZE).toInt()
            val yDiv = (position.y / Constants.GOOD_VOXEL_SIZE).toInt()
            var x = Constants.GOOD_VOXEL_SIZE * xDiv
            var y = Constants.GOOD_VOXEL_SIZE * yDiv
            if (position.x < 0) {
                x -= Constants.GOOD_VOXEL_SIZE
            }
            if (position.y < 0) {
                y -= Constants.GOOD_VOXEL_SIZE
            }
            return Vector2D(x, y)
        }

        private fun drawMotionOnVoxelMap(
            voxelMap: RedBlackTree<Vector2D, Vector<Motion>>, motion: Motion
        ) {
            val seen = RedBlackTree<Vector2D, Boolean>()
            recurse(voxelMap, seen, voxelHash(motion.posOne), motion)
        }
    }
}