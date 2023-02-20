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

import macroBenchmarks.som.ForEachInterface

class RedBlackTree<K : Comparable<K>, V> {
    var root: Node<K, V>? = null

    enum class Color {
        RED, BLACK
    }

    class Node<K, V> internal constructor(val key: K, var value: V) {
        var left: Node<K, V>? = null
        var right: Node<K, V>? = null
        var parent: Node<K, V>? = null
        var color: Color = Color.RED

        fun successor(): Node<K, V>? {
            var x = this
            val xRight = x.right
            if (xRight != null) {
                return treeMinimum(xRight)
            }
            var y = x.parent
            while (y != null && x == y.right) {
                x = y
                y = y.parent
            }
            return y
        }
    }

    fun put(key: K, value: V): V? {
        val insertionResult = treeInsert(key, value)
        if (!insertionResult.isNewEntry) {
            return insertionResult.oldValue
        }

        var x = insertionResult.newNode!!
        while (x != root) {
            val xParent = x.parent!!
            if (xParent.color != Color.RED) break
            val xParentParent = xParent.parent!!

            if (xParent == xParentParent.left) {
                val y = xParentParent.right
                if (y != null && y.color == Color.RED) {
                    // Case 1
                    xParent.color = Color.BLACK
                    y.color = Color.BLACK
                    xParentParent.color = Color.RED
                    x = xParentParent
                } else {
                    if (x == xParent.right) {
                        // Case 2
                        x = xParent
                        leftRotate(x)
                    }
                    // Case 3
                    val newXParent = x.parent!!
                    val newXParentParent = newXParent.parent!!
                    newXParent.color = Color.BLACK
                    newXParentParent.color = Color.RED
                    rightRotate(newXParentParent)
                }
            } else {
                // Same as "then" clause with "right" and "left" exchanged.
                val y = xParentParent.left
                if (y != null && y.color == Color.RED) {
                    // Case 1
                    xParent.color = Color.BLACK
                    y.color = Color.BLACK
                    xParentParent.color = Color.RED
                    x = xParentParent
                } else {
                    if (x == xParent.left) {
                        // Case 2
                        x = xParent
                        rightRotate(x)
                    }
                    // Case 3
                    val newXParent = x.parent!!
                    val newXParentParent = newXParent.parent!!
                    newXParent.color = Color.BLACK
                    newXParentParent.color = Color.RED
                    leftRotate(newXParentParent)
                }
            }
        }
        root!!.color = Color.BLACK
        return null
    }

    fun remove(key: K): V? {
        val z = findNode(key) ?: return null

        // Y is the node to be unlinked from the tree.
        val y: Node<K, V> = if (z.left == null || z.right == null) z else z.successor()!!

        // Y is guaranteed to be non-null at this point.
        val x: Node<K, V>? = y.left ?: y.right

        // X is the child of y which might potentially replace y in the tree. X might be null at
        // this point.
        val xParent: Node<K, V>?
        if (x != null) {
            x.parent = y.parent
            xParent = x.parent
        } else {
            xParent = y.parent
        }
        val yParent = y.parent
        if (yParent == null) {
            root = x
        } else {
            if (y == yParent.left) {
                yParent.left = x
            } else {
                yParent.right = x
            }
        }
        if (y != z) {
            if (y.color == Color.BLACK) {
                removeFixup(x, xParent)
            }
            y.parent = z.parent
            y.color = z.color
            y.left = z.left
            y.right = z.right
            val zLeft = z.left
            if (zLeft != null) {
                zLeft.parent = y
            }
            val zRight = z.right
            if (zRight != null) {
                zRight.parent = y
            }
            val zParent = z.parent
            if (zParent != null) {
                if (zParent.left == z) {
                    zParent.left = y
                } else {
                    zParent.right = y
                }
            } else {
                root = y
            }
        } else if (y.color == Color.BLACK) {
            removeFixup(x, xParent)
        }
        return z.value
    }

    operator fun get(key: K): V? {
        val node = findNode(key) ?: return null
        return node.value
    }

    class Entry<K, V>(val key: K, val value: V)

    fun forEach(fn: ForEachInterface<Entry<K, V>>) {
        val currentRoot = root ?: return
        var current: Node<K, V>? = treeMinimum(currentRoot)
        while (current != null) {
            fn.apply(Entry(current.key, current.value))
            current = current.successor()
        }
    }

    private fun findNode(key: K): Node<K, V>? {
        var current = root
        while (current != null) {
            val comparisonResult = key.compareTo(current.key)
            if (comparisonResult == 0) {
                return current
            }
            current = if (comparisonResult < 0) {
                current.left
            } else {
                current.right
            }
        }
        return null
    }

    private class InsertResult<K, V>(
        val isNewEntry: Boolean,
        val newNode: Node<K, V>?,
        val oldValue: V?
    )

    private fun treeInsert(key: K, value: V): InsertResult<K, V> {
        var y: Node<K, V>? = null
        var x = root
        while (x != null) {
            y = x
            val comparisonResult = key.compareTo(x.key)
            if (comparisonResult < 0) {
                x = x.left
            } else if (comparisonResult > 0) {
                x = x.right
            } else {
                val oldValue = x.value
                x.value = value
                return InsertResult(false, null, oldValue)
            }
        }
        val z = Node(key, value)
        z.parent = y
        if (y == null) {
            root = z
        } else {
            if (key < y.key) {
                y.left = z
            } else {
                y.right = z
            }
        }
        return InsertResult(true, z, null)
    }

    private fun leftRotate(x: Node<K, V>): Node<K, V> {
        val y = x.right

        // Turn y's left subtree into x's right subtree.
        x.right = y!!.left
        val yLeft = y.left
        if (yLeft != null) {
            yLeft.parent = x
        }

        // Link x's parent to y.
        y.parent = x.parent
        val xParent = x.parent
        if (xParent == null) {
            root = y
        } else {
            if (x == xParent.left) {
                xParent.left = y
            } else {
                xParent.right = y
            }
        }

        // Put x on y's left.
        y.left = x
        x.parent = y
        return y
    }

    private fun rightRotate(y: Node<K, V>): Node<K, V> {
        val x = y.left!!

        // Turn x's right subtree into y's left subtree.
        y.left = x.right
        val xRight = x.right
        if (xRight != null) {
            xRight.parent = y
        }

        // Link y's parent to x;
        x.parent = y.parent
        val yParent = y.parent
        if (yParent == null) {
            root = x
        } else {
            if (y == yParent.left) {
                yParent.left = x
            } else {
                yParent.right = x
            }
        }
        x.right = y
        y.parent = x
        return x
    }

    private fun removeFixup(node: Node<K, V>?, parent: Node<K, V>?) {
        var x = node
        var xParent = parent
        while (x != root && (x == null || x.color == Color.BLACK)) {
            if (x == xParent!!.left) {
                // Note: the text points out that w cannot be null. The reason is not obvious from
                // simply looking at the code; it comes about from the properties of the red-black
                // tree.
                var w = xParent.right
                if (w!!.color == Color.RED) {
                    // Case 1
                    w.color = Color.BLACK
                    xParent.color = Color.RED
                    leftRotate(xParent)
                    w = xParent.right
                }
                if ((w!!.left == null || w.left!!.color == Color.BLACK)
                    && (w.right == null || w.right!!.color == Color.BLACK)
                ) {
                    // Case 2
                    w.color = Color.RED
                    x = xParent
                    xParent = x.parent
                } else {
                    if (w.right == null || w.right!!.color == Color.BLACK) {
                        // Case 3
                        w.left!!.color = Color.BLACK
                        w.color = Color.RED
                        rightRotate(w)
                        w = xParent.right
                    }
                    // Case 4
                    w!!.color = xParent.color
                    xParent.color = Color.BLACK
                    if (w.right != null) {
                        w.right!!.color = Color.BLACK
                    }
                    leftRotate(xParent)
                    x = root
                    xParent = x!!.parent
                }
            } else {
                // Same as "then" clause with "right" and "left" exchanged.
                var w = xParent.left
                if (w!!.color == Color.RED) {
                    // Case 1
                    w.color = Color.BLACK
                    xParent.color = Color.RED
                    rightRotate(xParent)
                    w = xParent.left
                }
                if ((w!!.right == null || w.right!!.color == Color.BLACK)
                    && (w.left == null || w.left!!.color == Color.BLACK)
                ) {
                    // Case 2
                    w.color = Color.RED
                    x = xParent
                    xParent = x.parent
                } else {
                    if (w.left == null || w.left!!.color == Color.BLACK) {
                        // Case 3
                        w.right!!.color = Color.BLACK
                        w.color = Color.RED
                        leftRotate(w)
                        w = xParent.left
                    }
                    // Case 4
                    w!!.color = xParent.color
                    xParent.color = Color.BLACK
                    if (w.left != null) {
                        w.left!!.color = Color.BLACK
                    }
                    rightRotate(xParent)
                    x = root
                    xParent = x!!.parent
                }
            }
        }
        if (x != null) {
            x.color = Color.BLACK
        }
    }

    companion object {
        private fun <K, V> treeMinimum(x: Node<K, V>): Node<K, V> {
            var current = x
            while (current.left != null) {
                current = current.left ?: break
            }
            return current
        }
    }
}