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

import macroBenchmarks.som.Dictionary.CustomHash
import macroBenchmarks.som.Vector

/*
 * A simple class simulating the concept of Basic Blocks
 *
 * BasicBlock only maintains a vector of in-edges and
 * a vector of out-edges.
 *
 * @author rhundt
 */
class BasicBlock(private val name: Int) : CustomHash {
    val inEdges: Vector<BasicBlock> = Vector(2)
    val outEdges: Vector<BasicBlock> = Vector(2)

    val numPred: Int
        get() = inEdges.size

    fun addOutEdge(to: BasicBlock) {
        outEdges.append(to)
    }

    fun addInEdge(from: BasicBlock) {
        inEdges.append(from)
    }

    override fun customHash(): Int = name
}