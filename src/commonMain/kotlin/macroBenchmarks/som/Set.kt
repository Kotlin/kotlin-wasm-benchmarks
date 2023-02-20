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

open class Set<E> constructor(size: Int = Constants.INITIAL_SIZE) {
    private val items: Vector<E> = Vector(size)

    val size: Int
        get() = items.size

    fun forEach(fn: ForEachInterface<E>) {
        items.forEach(fn)
    }

    fun hasSome(fn: TestInterface<E>): Boolean =
        items.hasSome(fn)

    fun getOne(fn: TestInterface<E>): E? =
        items.getOne(fn)

    fun add(obj: E) {
        if (!contains(obj)) {
            items.append(obj)
        }
    }

    fun <T> collect(fn: CollectInterface<E, T>): Vector<T> {
        val coll = Vector<T>()
        forEach { e -> coll.append(fn.collect(e)) }
        return coll
    }

    open operator fun contains(obj: E): Boolean =
        hasSome { e: E? -> e == obj }

    fun removeAll() {
        items.removeAll()
    }
}