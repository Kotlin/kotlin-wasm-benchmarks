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

import macroBenchmarks.som.Dictionary.CustomHash

open class Dictionary<K : CustomHash?, V> constructor(size: Int = INITIAL_CAPACITY) {
    interface CustomHash {
        fun customHash(): Int
    }

    private var buckets: Array<Entry<K, V>?> = arrayOfNulls<Entry<K, V>?>(size)
    private var size = 0

    open class Entry<K, V>(val hash: Int, val key: K, var value: V, var next: Entry<K, V>?) {
        open fun match(hash: Int, key: K): Boolean {
            return this.hash == hash && key == this.key
        }
    }

    fun size(): Int {
        return size
    }

    val isEmpty: Boolean
        get() = size == 0

    private fun getBucketIdx(hash: Int): Int {
        return buckets.size - 1 and hash
    }

    private fun getBucket(hash: Int): Entry<K, V>? {
        return buckets[getBucketIdx(hash)]
    }

    fun at(key: K): V? {
        val hash = hash<K>(key)
        var e = getBucket(hash)
        while (e != null) {
            if (e.match(hash, key)) {
                return e.value
            }
            e = e.next
        }
        return null
    }

    fun containsKey(key: K): Boolean {
        val hash = hash<K>(key)
        var e = getBucket(hash)
        while (e != null) {
            if (e.match(hash, key)) {
                return true
            }
            e = e.next
        }
        return false
    }

    fun atPut(key: K, value: V) {
        val hash = hash<K>(key)
        val i = getBucketIdx(hash)
        val current = buckets[i]
        if (current == null) {
            buckets[i] = newEntry(key, value, hash)
            size += 1
        } else {
            insertBucketEntry(key, value, hash, current)
        }
        if (size > buckets.size) {
            resize()
        }
    }

    protected open fun newEntry(key: K, value: V, hash: Int): Entry<K, V>? {
        return Entry(hash, key, value, null)
    }

    private fun insertBucketEntry(
        key: K,
        value: V, hash: Int, head: Entry<K, V>
    ) {
        var current: Entry<K, V>? = head
        while (true) {
            if (current!!.match(hash, key)) {
                current.value = value
                return
            }
            if (current.next == null) {
                size += 1
                current.next = newEntry(key, value, hash)
                return
            }
            current = current.next
        }
    }

    private fun resize() {
        val oldStorage = buckets
        val newStorage: Array<Entry<K, V>?> = arrayOfNulls<Entry<K, V>?>(oldStorage.size * 2)
        buckets = newStorage
        transferEntries(oldStorage)
    }

    private fun transferEntries(oldStorage: Array<Entry<K, V>?>) {
        for (i in oldStorage.indices) {
            val current = oldStorage[i]
            if (current != null) {
                oldStorage[i] = null
                if (current.next == null) {
                    buckets[current.hash and buckets.size - 1] = current
                } else {
                    splitBucket(oldStorage, i, current)
                }
            }
        }
    }

    private fun splitBucket(
        oldStorage: Array<Entry<K, V>?>, i: Int,
        head: Entry<K, V>
    ) {
        var loHead: Entry<K, V>? = null
        var loTail: Entry<K, V>? = null
        var hiHead: Entry<K, V>? = null
        var hiTail: Entry<K, V>? = null
        var current: Entry<K, V>? = head
        while (current != null) {
            if (current.hash and oldStorage.size == 0) {
                if (loTail == null) {
                    loHead = current
                } else {
                    loTail.next = current
                }
                loTail = current
            } else {
                if (hiTail == null) {
                    hiHead = current
                } else {
                    hiTail.next = current
                }
                hiTail = current
            }
            current = current.next
        }
        if (loTail != null) {
            loTail.next = null
            buckets[i] = loHead
        }
        if (hiTail != null) {
            hiTail.next = null
            buckets[i + oldStorage.size] = hiHead
        }
    }

    fun removeAll() {
        buckets = arrayOfNulls<Entry<K, V>?>(buckets.size)
        size = 0
    }

    val keys: Vector<K>
        get() {
            val keys = Vector<K>(size)
            for (i in buckets.indices) {
                var current = buckets[i]
                while (current != null) {
                    keys.append(current.key)
                    current = current.next
                }
            }
            return keys
        }
    val values: Vector<V>
        get() {
            val values = Vector<V>(size)
            for (i in buckets.indices) {
                var current = buckets[i]
                while (current != null) {
                    values.append(current.value)
                    current = current.next
                }
            }
            return values
        }

    companion object {
        const val INITIAL_CAPACITY = 16
        private fun <K : CustomHash?> hash(key: K?): Int {
            if (key == null) {
                return 0
            }
            val hash = key.customHash()
            return hash xor (hash ushr 16)
        }
    }
}