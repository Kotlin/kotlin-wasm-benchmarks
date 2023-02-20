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

import macroBenchmarks.som.IdentityDictionary
import macroBenchmarks.som.Set
import macroBenchmarks.som.Vector

/*
 * The Havlak loop finding algorithm.
 *
 * This class encapsulates the complete finder algorithm
 *
 * @author rhundt
 */
class HavlakLoopFinder(// Control Flow Graph
    private val cfg: ControlFlowGraph, // Loop Structure Graph
    private val lsg: LoopStructureGraph
) {
    private val nonBackPreds = Vector<Set<Int>>()
    private val backPreds = Vector<Vector<Int>>()
    private val number = IdentityDictionary<BasicBlock, Int>()
    private var maxSize = 0
    private var header: IntArray = intArrayOf()
    private var type: Array<BasicBlockClass?> = arrayOf()
    private var last: IntArray = intArrayOf()
    private var nodes: Array<UnionFindNode> = arrayOf()

    /*
     * enum BasicBlockClass
     *
     * Basic Blocks and Loops are being classified as regular, irreducible,
     * and so on. This enum contains a symbolic name for all these classifications
     */
    private enum class BasicBlockClass {
        @Suppress("unused")
        BB_TOP,  // uninitialized
        BB_NONHEADER,  // a regular BB
        BB_REDUCIBLE,  // reducible loop
        BB_SELF,  // single BB loop
        BB_IRREDUCIBLE,  // irreducible loop
        BB_DEAD,  // a dead BB
        @Suppress("unused")
        BB_LAST // Sentinel
    }

    //
    // IsAncestor
    //
    // As described in the paper, determine whether a node 'w' is a
    // "true" ancestor for node 'v'.
    //
    // Dominance can be tested quickly using a pre-order trick
    // for depth-first spanning trees. This is why DFS is the first
    // thing we run below.
    //
    private fun isAncestor(w: Int, v: Int): Boolean {
        return w <= v && v <= last[w]
    }

    //
    // DFS - Depth-First-Search
    //
    // DESCRIPTION:
    // Simple depth first traversal along out edges with node numbering.
    //
    private fun doDFS(currentNode: BasicBlock, current: Int): Int {
        nodes[current].initNode(currentNode, current)
        number.atPut(currentNode, current)
        var lastId = current
        val outerBlocks = currentNode.outEdges
        for (i in 0 until outerBlocks.size) {
            val target = outerBlocks.at(i)!!
            if (number.at(target) == UNVISITED) {
                lastId = doDFS(target, lastId + 1)
            }
        }
        last[current] = lastId
        return lastId
    }

    private fun initAllNodes() {
        // Step a:
        //   - initialize all nodes as unvisited.
        //   - depth-first traversal and numbering.
        //   - unreached BB's are marked as dead.
        //
        cfg.basicBlocks.forEach { bb -> number.atPut(bb, UNVISITED) }
        doDFS(cfg.startBasicBlock!!, 0)
    }

    private fun identifyEdges(size: Int) {
        // Step b:
        //   - iterate over all nodes.
        //
        //   A backedge comes from a descendant in the DFS tree, and non-backedges
        //   from non-descendants (following Tarjan).
        //
        //   - check incoming edges 'v' and add them to either
        //     - the list of backedges (backPreds) or
        //     - the list of non-backedges (nonBackPreds)
        //
        for (w in 0 until size) {
            header[w] = 0
            type[w] = BasicBlockClass.BB_NONHEADER
            val nodeW = nodes[w].bb
            if (nodeW == null) {
                type[w] = BasicBlockClass.BB_DEAD
            } else {
                processEdges(nodeW, w)
            }
        }
    }

    private fun processEdges(nodeW: BasicBlock, w: Int) {
        if (nodeW.numPred > 0) {
            nodeW.inEdges.forEach { nodeV ->
                val v = number.at(nodeV)
                if (v != UNVISITED) {
                    if (isAncestor(w, v!!)) {
                        backPreds.at(w)!!.append(v)
                    } else {
                        nonBackPreds.at(w)!!.add(v)
                    }
                }
            }
        }
    }

    //
    // findLoops
    //
    // Find loops and build loop forest using Havlak's algorithm, which
    // is derived from Tarjan. Variable names and step numbering has
    // been chosen to be identical to the nomenclature in Havlak's
    // paper (which, in turn, is similar to the one used by Tarjan).
    //
    fun findLoops() {
        if (cfg.startBasicBlock == null) {
            return
        }
        val size = cfg.numNodes
        nonBackPreds.removeAll()
        backPreds.removeAll()
        number.removeAll()
        if (size > maxSize) {
            header = IntArray(size)
            type = arrayOfNulls(size)
            last = IntArray(size)
            nodes = Array(size) { UnionFindNode() }
            maxSize = size
        }
        for (i in 0 until size) {
            nonBackPreds.append(Set())
            backPreds.append(Vector())
        }
        initAllNodes()
        identifyEdges(size)

        // Start node is root of all other loops.
        header[0] = 0

        // Step c:
        //
        // The outer loop, unchanged from Tarjan. It does nothing except
        // for those nodes which are the destinations of backedges.
        // For a header node w, we chase backward from the sources of the
        // backedges adding nodes to the set P, representing the body of
        // the loop headed by w.
        //
        // By running through the nodes in reverse of the DFST preorder,
        // we ensure that inner loop headers will be processed before the
        // headers for surrounding loops.
        //
        for (w in size - 1 downTo 0) {
            // this is 'P' in Havlak's paper
            val nodePool = Vector<UnionFindNode?>()
            val nodeW = nodes[w].bb
            if (nodeW != null) {
                stepD(w, nodePool)

                // Copy nodePool to workList.
                //
                val workList = Vector<UnionFindNode?>()
                nodePool.forEach { niter: UnionFindNode? -> workList.append(niter) }
                if (nodePool.size != 0) {
                    type[w] = BasicBlockClass.BB_REDUCIBLE
                }

                // work the list...
                //
                while (!workList.isEmpty) {
                    val x = workList.removeFirst()

                    // Step e:
                    //
                    // Step e represents the main difference from Tarjan's method.
                    // Chasing upwards from the sources of a node w's backedges. If
                    // there is a node y' that is not a descendant of w, w is marked
                    // the header of an irreducible loop, there is another entry
                    // into this loop that avoids w.
                    //

                    // The algorithm has degenerated. Break and
                    // return in this case.
                    //
                    val nonBackSize = nonBackPreds.at(x!!.dfsNumber)!!.size
                    if (nonBackSize > MAXNONBACKPREDS) {
                        return
                    }
                    stepEProcessNonBackPreds(w, nodePool, workList, x)
                }

                // Collapse/Unionize nodes in a SCC to a single node
                // For every SCC found, create a loop descriptor and link it in.
                //
                if (nodePool.size > 0 || type[w] == BasicBlockClass.BB_SELF) {
                    val loop = lsg.createNewLoop(nodeW, type[w] != BasicBlockClass.BB_IRREDUCIBLE)
                    setLoopAttributes(w, nodePool, loop)
                }
            }
        } // Step c
    } // findLoops

    private fun stepEProcessNonBackPreds(
        w: Int, nodePool: Vector<UnionFindNode?>,
        workList: Vector<UnionFindNode?>, x: UnionFindNode?
    ) {
        nonBackPreds.at(x!!.dfsNumber)!!.forEach { iter: Int? ->
            val y = nodes[iter!!]
            val ydash = y.findSet()
            if (!isAncestor(w, ydash.dfsNumber)) {
                type[w] = BasicBlockClass.BB_IRREDUCIBLE
                nonBackPreds.at(w)!!.add(ydash.dfsNumber)
            } else {
                if (ydash.dfsNumber != w) {
                    if (!nodePool.hasSome { e: UnionFindNode? -> e == ydash }) {
                        workList.append(ydash)
                        nodePool.append(ydash)
                    }
                }
            }
        }
    }

    private fun setLoopAttributes(
        w: Int, nodePool: Vector<UnionFindNode?>,
        loop: SimpleLoop?
    ) {
        // At this point, one can set attributes to the loop, such as:
        //
        // the bottom node:
        //    iter  = backPreds[w].begin();
        //    loop bottom is: nodes[iter].node);
        //
        // the number of backedges:
        //    backPreds[w].size()
        //
        // whether this loop is reducible:
        //    type[w] != BasicBlockClass.BB_IRREDUCIBLE
        //
        nodes[w].loop = loop
        nodePool.forEach { node: UnionFindNode? ->
            // Add nodes to loop descriptor.
            header[node!!.dfsNumber] = w
            node.union(nodes[w])

            // Nested loops are not added, but linked together.
            val nodeLoop = node.loop
            if (nodeLoop != null) {
                nodeLoop.parent = loop
            } else {
                loop!!.addNode(node.bb)
            }
        }
    }

    private fun stepD(w: Int, nodePool: Vector<UnionFindNode?>) {
        backPreds.at(w)!!.forEach { v ->
            if (v != w) {
                nodePool.append(nodes[v].findSet())
            } else {
                type[w] = BasicBlockClass.BB_SELF
            }
        }
    }

    companion object {
        // Marker for uninitialized nodes.
        private const val UNVISITED = Int.MAX_VALUE

        // Safeguard against pathological algorithm behavior.
        private const val MAXNONBACKPREDS = 32 * 1024
    }
}