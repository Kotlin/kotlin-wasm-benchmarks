/*
 *  Copyright 2011 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package macroBenchmarks.havlak

import macroBenchmarks.som.Vector

/*
 * LoopStructureGraph
 *
 * Maintain loop structure for a given CFG.
 *
 * Two values are maintained for this loop graph, depth, and nesting level.
 * For example:
 *
 * loop        nesting level    depth
 * ----------------------------------------
 * loop-0      2                0
 * loop-1    1                1
 * loop-3    1                1
 * loop-2  0                2
 *
 * @author rhundt
 */
class LoopStructureGraph {
    private val root: SimpleLoop
    private val loops: Vector<SimpleLoop> = Vector()
    private var loopCounter = 0

    init {
        root = createNewLoop(null, true)
        root.nestingLevel = 0
    }

    fun createNewLoop(bb: BasicBlock?, isReducible: Boolean): SimpleLoop {
        val loop = SimpleLoop(bb, isReducible)
        loop.counter = loopCounter
        loopCounter += 1
        loops.append(loop)
        return loop
    }

    fun calculateNestingLevel() {
        // link up all 1st level loops to artificial root node.
        loops.forEach { liter ->
            if (!liter.isRoot) {
                if (liter.parent == null) {
                    liter.parent = root
                }
            }
        }

        // recursively traverse the tree and assign levels.
        calculateNestingLevelRec(root, 0)
    }

    private fun calculateNestingLevelRec(loop: SimpleLoop, depth: Int) {
        loop.setDepthLevel(depth)
        loop.children.forEach { liter ->
            calculateNestingLevelRec(liter, depth + 1)
            loop.nestingLevel = maxOf(loop.nestingLevel, 1 + liter.nestingLevel)
        }
    }

    val numLoops: Int
        get() = loops.size
}