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

class LoopTesterApp {
    private val cfg: ControlFlowGraph = ControlFlowGraph()
    private val lsg: LoopStructureGraph = LoopStructureGraph()

    init {
        cfg.createNode(0)
    }

    // Create 4 basic blocks, corresponding to and if/then/else clause
    // with a CFG that looks like a diamond
    private fun buildDiamond(start: Int): Int {
        BasicBlockEdge(cfg, start, start + 1)
        BasicBlockEdge(cfg, start, start + 2)
        BasicBlockEdge(cfg, start + 1, start + 3)
        BasicBlockEdge(cfg, start + 2, start + 3)
        return start + 3
    }

    // Connect two existing nodes
    private fun buildConnect(start: Int, end: Int) {
        BasicBlockEdge(cfg, start, end)
    }

    // Form a straight connected sequence of n basic blocks
    private fun buildStraight(start: Int, n: Int): Int {
        for (i in 0 until n) {
            buildConnect(start + i, start + i + 1)
        }
        return start + n
    }

    // Construct a simple loop with two diamonds in it
    private fun buildBaseLoop(from: Int): Int {
        val header = buildStraight(from, 1)
        val diamond1 = buildDiamond(header)
        val d11 = buildStraight(diamond1, 1)
        val diamond2 = buildDiamond(d11)
        var footer = buildStraight(diamond2, 1)
        buildConnect(diamond2, d11)
        buildConnect(diamond1, header)
        buildConnect(footer, from)
        footer = buildStraight(footer, 1)
        return footer
    }

    fun main(
        numDummyLoops: Int, findLoopIterations: Int,
        parLoops: Int, pparLoops: Int, ppparLoops: Int
    ): IntArray {
        constructSimpleCFG()
        addDummyLoops(numDummyLoops)
        constructCFG(parLoops, pparLoops, ppparLoops)

        // Performing Loop Recognition, 1 Iteration, then findLoopIteration
        findLoops(lsg)
        for (i in 0 until findLoopIterations) {
            findLoops(LoopStructureGraph())
        }
        lsg.calculateNestingLevel()
        return intArrayOf(lsg.numLoops, cfg.numNodes)
    }

    private fun constructCFG(parLoops: Int, pparLoops: Int, ppparLoops: Int) {
        var n = 2
        for (parlooptrees in 0 until parLoops) {
            cfg.createNode(n + 1)
            buildConnect(2, n + 1)
            n += 1
            for (i in 0 until pparLoops) {
                val top = n
                n = buildStraight(n, 1)
                for (j in 0 until ppparLoops) {
                    n = buildBaseLoop(n)
                }
                val bottom = buildStraight(n, 1)
                buildConnect(n, top)
                n = bottom
            }
            buildConnect(n, 1)
        }
    }

    private fun addDummyLoops(numDummyLoops: Int) {
        for (dummyLoop in 0 until numDummyLoops) {
            findLoops(lsg)
        }
    }

    private fun findLoops(loopStructure: LoopStructureGraph) {
        val finder = HavlakLoopFinder(cfg, loopStructure)
        finder.findLoops()
    }

    private fun constructSimpleCFG() {
        cfg.createNode(0)
        buildBaseLoop(0)
        cfg.createNode(1)
        BasicBlockEdge(cfg, 0, 2)
    }
}