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
 * A simple class simulating the concept of
 * a control flow graph.
 *
 * CFG maintains a list of nodes, plus a start node.
 * That's it.
 *
 * @author rhundt
 */
class ControlFlowGraph {
    val basicBlocks: Vector<BasicBlock> = Vector()
    var startBasicBlock: BasicBlock? = null
        private set
    private val edgeList: Vector<BasicBlockEdge> = Vector()

    fun createNode(name: Int): BasicBlock {
        val node: BasicBlock
        val blockWithName = basicBlocks.at(name)
        if (blockWithName != null) {
            node = blockWithName
        } else {
            node = BasicBlock(name)
            basicBlocks.atPut(name, node)
        }
        if (numNodes == 1) {
            startBasicBlock = node
        }
        return node
    }

    fun addEdge(edge: BasicBlockEdge) {
        edgeList.append(edge)
    }

    val numNodes: Int
        get() = basicBlocks.size
}