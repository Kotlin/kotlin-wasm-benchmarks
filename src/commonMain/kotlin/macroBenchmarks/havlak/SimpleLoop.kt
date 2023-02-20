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

/*
 * The Havlak loop finding algorithm.
 *
 * @author rhundt
 */

package macroBenchmarks.havlak

import macroBenchmarks.som.IdentitySet

/*
 * class SimpleLoop
 *
 * Basic representation of loops, a loop has an entry point,
 * one or more exit edges, a set of basic blocks, and potentially
 * an outer loop - a "parent" loop.
 *
 * Furthermore, it can have any set of properties, e.g.,
 * it can be an irreducible loop, have control flow, be
 * a candidate for transformations, and what not.
 */
class SimpleLoop(bb: BasicBlock?, private val isReducible: Boolean) {
    private val basicBlocks: IdentitySet<BasicBlock?> = IdentitySet()

    // Getters/Setters
    val children: IdentitySet<SimpleLoop> = IdentitySet()

    private var _parent: SimpleLoop? = null
    var parent: SimpleLoop?
        get() = _parent
        set(value) {
            _parent = value
            _parent!!.addChildLoop(this)
        }
    private val header: BasicBlock?

    // Note: fct and var are same!
    var isRoot = false
        private set

    private var _nestingLevel = 0
    var nestingLevel
        get() = _nestingLevel
        set(level) {
            _nestingLevel = level
            if (level == 0) {
                isRoot = true
            }
        }

    var counter = 0
    private var depthLevel = 0

    init {
        if (bb != null) {
            basicBlocks.add(bb)
        }
        header = bb
    }

    fun addNode(bb: BasicBlock?) {
        basicBlocks.add(bb)
    }

    private fun addChildLoop(loop: SimpleLoop) {
        children.add(loop)
    }

    fun setDepthLevel(level: Int) {
        depthLevel = level
    }
}