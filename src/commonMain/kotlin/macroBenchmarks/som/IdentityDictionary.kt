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

class IdentityDictionary<K : CustomHash?, V> : Dictionary<K, V> {
    class IdEntry<K, V>(hash: Int, key: K, value: V, next: Entry<K, V>?) :
        Entry<K, V>(hash, key, value, next) {
        override fun match(hash: Int, key: K): Boolean {
            return this.hash == hash && this.key === key
        }
    }

    constructor(size: Int) : super(size)
    constructor() : super(INITIAL_CAPACITY)

    override fun newEntry(key: K, value: V, hash: Int): Entry<K, V> {
        return IdEntry(hash, key, value, null)
    }
}