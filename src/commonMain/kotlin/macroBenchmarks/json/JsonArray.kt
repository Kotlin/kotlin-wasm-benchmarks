/******************************************************************************
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
 * Represents a JSON array, an ordered collection of JSON values.
 *
 *
 * Elements can be added using the `add(...)` methods which accept instances of
 * [JsonValue], strings, primitive numbers, and boolean values. To replace an element of an
 * array, use the `set(int, ...)` methods.
 *
 *
 *
 * Note that this class is **not thread-safe**. If multiple threads access a
 * `JsonArray` instance concurrently, while at least one of these threads modifies the
 * contents of this array, access to the instance must be synchronized externally. Failure to do so
 * may lead to an inconsistent state.
 *
 *
 *
 * This class is **not supposed to be extended** by clients.
 *
 */
class JsonArray : JsonValue() {
    private val values: Vector<JsonValue> = Vector()

    /*
     * Appends the specified JSON value to the end of this array.
     *
     * @param value
     * the JsonValue to add to the array, must not be `null`
     * @return the array itself, to enable method chaining
     */
    fun add(value: JsonValue?): JsonArray {
        if (value == null) {
            throw NullPointerException("value is null")
        }
        values.append(value)
        return this
    }

    /*
     * Returns the number of elements in this array.
     *
     * @return the number of elements in this array
     */
    fun size(): Int = values.size

    /*
     * Returns the value of the element at the specified position in this array.
     *
     * @param index
     * the index of the array element to return
     * @return the value of the element at the specified position
     * @throws IndexOutOfBoundsException
     * if the index is out of range, i.e. `index < 0` or
     * `index >= size`
     */
    operator fun get(index: Int): JsonValue = values.at(index)!!

    override val isArray: Boolean = true

    override fun asArray(): JsonArray = this
}