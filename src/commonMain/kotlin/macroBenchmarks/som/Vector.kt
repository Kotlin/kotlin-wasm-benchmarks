/* This code is based on the SOM class library.
 *
 * Copyright (c) 2001-2016 see AUTHORS.md file
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

package macroBenchmarks.som

/*
 * Porting notes:
 * - does not use an explicit array bounds check, because Java already does
 * that. Don't see a point in doing it twice.
 */
open class Vector<E> constructor(size: Int = 50) {
    private var storage: Array<Any?> = arrayOfNulls(size)
    private var firstIdx = 0
    private var lastIdx = 0

    @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
    private inline fun Array<Any?>.getAt(idx: Int): E =
        this[idx] as E

    @Suppress("UNCHECKED_CAST")
    fun at(idx: Int): E? =
        if (idx >= storage.size) null else storage[idx] as E?

    fun atPut(idx: Int, value: E) {
        if (idx >= storage.size) {
            var newLength = storage.size
            while (newLength <= idx) {
                newLength *= 2
            }
            storage = storage.copyOf(newLength)
        }
        storage[idx] = value
        if (lastIdx < idx + 1) {
            lastIdx = idx + 1
        }
    }

    fun append(elem: E) {
        if (lastIdx >= storage.size) {
            // Need to expand capacity first
            storage = storage.copyOf(2 * storage.size)
        }
        storage[lastIdx] = elem
        lastIdx++
    }

    val isEmpty: Boolean
        get() = lastIdx == firstIdx

    fun forEach(fn: ForEachInterface<E>) {
        for (i in firstIdx until lastIdx) {
            fn.apply(storage.getAt(i))
        }
    }

    fun hasSome(fn: TestInterface<E>): Boolean {
        for (i in firstIdx until lastIdx) {
            if (fn.test(storage.getAt(i))) {
                return true
            }
        }
        return false
    }

    @Suppress("unused")
    fun getOne(fn: TestInterface<E>): E? {
        for (i in firstIdx until lastIdx) {
            val e = storage.getAt(i)
            if (fn.test(e)) {
                return e
            }
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    fun first(): E? =
        if (isEmpty) null else storage[firstIdx] as E?

    @Suppress("UNCHECKED_CAST")
    fun removeFirst(): E? {
        if (isEmpty) return null
        firstIdx++
        return storage[firstIdx - 1] as E?
    }

    fun remove(obj: E): Boolean {
        val newArray = arrayOfNulls<Any>(capacity())
        val newLast = intArrayOf(0)
        val found = booleanArrayOf(false)
        forEach {
            if (it === obj) {
                found[0] = true
            } else {
                newArray[newLast[0]] = it
                newLast[0]++
            }
        }
        storage = newArray
        lastIdx = newLast[0]
        firstIdx = 0
        return found[0]
    }

    fun removeAll() {
        firstIdx = 0
        lastIdx = 0
        storage = arrayOfNulls(storage.size)
    }

    val size: Int
        get() = lastIdx - firstIdx

    fun capacity(): Int =
        storage.size

    fun sort(c: Comparator<E>?) {
        if (size > 0) {
            sort(firstIdx, lastIdx - 1, c)
        }
    }

    private fun sort(i: Int, j: Int, c: Comparator<E>?) {
        if (c == null) {
            defaultSort(i, j)
            return
        }
        val n = j + 1 - i
        if (n <= 1) {
            return
        }
        var di = storage.getAt(i)
        var dj = storage.getAt(j)
        if (c.compare(di, dj) > 0) {
            swap(storage, i, j)
            val tt = di
            di = dj
            dj = tt
        }
        if (n > 2) {
            val ij = (i + j) / 2
            var dij = storage.getAt(ij)
            if (c.compare(di, dij) <= 0) {
                if (c.compare(dij, dj) > 0) {
                    swap(storage, j, ij)
                    dij = dj
                }
            } else {
                swap(storage, i, ij)
                dij = di
            }
            if (n > 3) {
                var k = i
                var l = j - 1
                while (true) {
                    while (k <= l && c.compare(dij, storage.getAt(l)) <= 0) {
                        l -= 1
                    }
                    k += 1
                    while (k <= l && c.compare(storage.getAt(k), dij) <= 0) {
                        k += 1
                    }
                    if (k > l) {
                        break
                    }
                    swap(storage, k, l)
                }
                sort(i, l, c)
                sort(k, j, c)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun defaultSort(i: Int, j: Int): Unit =
        throw NotImplementedError()

    companion object {
        fun <E> with(elem: E): Vector<E> {
            val v = Vector<E>(1)
            v.append(elem)
            return v
        }

        @Suppress("UNUSED_PARAMETER")
        private fun swap(storage2: Array<Any?>, i: Int, j: Int): Unit =
            throw NotImplementedError()
    }
}