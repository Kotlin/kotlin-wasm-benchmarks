/*
 * Copyright (c) 2013, 2015 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package macroBenchmarks.json

import macroBenchmarks.som.Vector

/*
 * Represents a JSON object, a set of name/value pairs, where the names are strings and the values
 * are JSON values.
 *
 *
 * Members can be added using the `add(String, ...)` methods which accept instances of
 * [JsonValue], strings, primitive numbers, and boolean values. To modify certain values of an
 * object, use the `set(String, ...)` methods. Please note that the `add`
 * methods are faster than `set` as they do not search for existing members. On the other
 * hand, the `add` methods do not prevent adding multiple members with the same name.
 * Duplicate names are discouraged but not prohibited by JSON.
 *
 *
 *
 * Members can be accessed by their name using [.get].
 *
 *
 * This class is **not supposed to be extended** by clients.
 *
 */
class JsonObject : JsonValue() {
    private val names: Vector<String> = Vector()
    private val values: Vector<JsonValue> = Vector()
    private val table: HashIndexTable = HashIndexTable()

    /*
     * Appends a new member to the end of this object, with the specified name and the specified JSON
     * value.
     *
     *
     * This method **does not prevent duplicate names**. Calling this method with a name
     * that already exists in the object will append another member with the same name. In order to
     * replace existing members, use the method `set(name, value)` instead. However,
     * ** *add* is much faster than *set*** (because it does not need to
     * search for existing members). Therefore *add* should be preferred when constructing new
     * objects.
     *
     *
     * @param name
     * the name of the member to add
     * @param value
     * the value of the member to add, must not be `null`
     * @return the object itself, to enable method chaining
     */
    fun add(name: String, value: JsonValue): JsonObject {
        table.add(name, names.size)
        names.append(name)
        values.append(value)
        return this
    }

    /*
     * Returns the value of the member with the specified name in this object. If this object contains
     * multiple members with the given name, this method will return the last one.
     *
     * @param name
     * the name of the member whose value is to be returned
     * @return the value of the last member with the specified name, or `null` if this
     * object does not contain a member with that name
     */
    operator fun get(name: String): JsonValue? {
        val index = indexOf(name)
        return if (index == -1) null else values.at(index)
    }

    /*
     * Returns the number of members (name/value pairs) in this object.
     *
     * @return the number of members in this object
     */
    val size: Int get() = names.size

    /*
     * Returns `true` if this object contains no members.
     *
     * @return `true` if this object contains no members
     */
    val isEmpty: Boolean
        get() = names.isEmpty

    override val isObject: Boolean = true

    override fun asObject(): JsonObject = this

    private fun indexOf(name: String): Int {
        val index = table[name]
        if (index != -1 && name == names.at(index)) {
            return index
        }
        throw NotImplementedError()
    }

    private class HashIndexTable {
        private val hashTable: IntArray = IntArray(32) // must be a power of two

        fun add(name: String, index: Int) {
            val slot = hashSlotFor(name)
            if (index < 0xff) {
                // increment by 1, 0 stands for empty
                hashTable[slot] = index + 1 and 0xff
            } else {
                hashTable[slot] = 0
            }
        }

        operator fun get(name: String): Int =
            // subtract 1, 0 stands for empty
            (hashTable[hashSlotFor(name)] and 0xff) - 1

        private fun stringHash(s: String): Int =
            // this is not a proper hash, but sufficient for the benchmark,
            // and very portable!
            s.length * 1402589

        private fun hashSlotFor(element: String): Int =
            stringHash(element) and hashTable.size - 1
    }
}