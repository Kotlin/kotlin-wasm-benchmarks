package macroBenchmarks.havlak

import macroBenchmarks.som.Vector

/*
 * class UnionFindNode
 *
 * The algorithm uses the Union/Find algorithm to collapse
 * complete loops into a single node. These nodes and the
 * corresponding functionality are implemented with this class
 */
class UnionFindNode {
    private var parent: UnionFindNode? = null

    // Getters/Setters
    //
    var bb: BasicBlock? = null
        private set
    var loop: SimpleLoop? = null
    var dfsNumber = 0
        private set

    // Initialize this node.
    //
    fun initNode(bb: BasicBlock?, dfsNumber: Int) {
        parent = this
        this.bb = bb
        this.dfsNumber = dfsNumber
        loop = null
    }

    // Union/Find Algorithm - The find routine.
    //
    // Implemented with Path Compression (inner loops are only
    // visited and collapsed once, however, deep nests would still
    // result in significant traversals).
    //
    fun findSet(): UnionFindNode {
        val nodeList = Vector<UnionFindNode>()
        var node: UnionFindNode = this
        val nodeParent = node.parent
        while (nodeParent != null && node != nodeParent) {
            if (nodeParent != nodeParent.parent) {
                nodeList.append(node)
            }
            node = nodeParent
        }

        // Path Compression, all nodes' parents point to the 1st level parent.
        nodeList.forEach { it.union(parent) }
        return node
    }

    // Union/Find Algorithm - The union routine.
    //
    // Trivial. Assigning parent pointer is enough,
    // we rely on path compression.
    //
    fun union(basicBlock: UnionFindNode?) {
        parent = basicBlock
    }
}