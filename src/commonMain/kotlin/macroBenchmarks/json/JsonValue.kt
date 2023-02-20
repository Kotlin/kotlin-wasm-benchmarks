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

/*
 * Represents a JSON value. This can be a JSON **object**, an ** array**,
 * a **number**, a **string**, or one of the literals
 * **true**, **false**, and **null**.
 *
 *
 * The literals **true**, **false**, and **null** are
 * represented by the constants [.TRUE], [.FALSE], and [.NULL].
 *
 *
 *
 * JSON **objects** and **arrays** are represented by the subtypes
 * [JsonObject] and [JsonArray]. Instances of these types can be created using the
 * public constructors of these classes.
 *
 *
 *
 * Instances that represent JSON **numbers**, **strings** and
 * **boolean** values can be created using the static factory methods
 * [.valueOf], [.valueOf], [.valueOf], etc.
 *
 *
 *
 * In order to find out whether an instance of this class is of a certain type, the methods
 * [.isObject], [.isArray], [.isString], [.isNumber] etc. can be
 * used.
 *
 *
 *
 * If the type of a JSON value is known, the methods [.asObject], [.asArray],
 * [.asString], [.asInt], etc. can be used to get this value directly in the
 * appropriate target type.
 *
 *
 *
 * This class is **not supposed to be extended** by clients.
 *
 */

abstract class JsonValue {
    /*
     * Detects whether this value represents a JSON object. If this is the case, this value is an
     * instance of [JsonObject].
     *
     * @return `true` if this value is an instance of JsonObject
     */
    open val isObject: Boolean
        get() = false

    /*
     * Detects whether this value represents a JSON array. If this is the case, this value is an
     * instance of [JsonArray].
     *
     * @return `true` if this value is an instance of JsonArray
     */
    open val isArray: Boolean
        get() = false

    /*
     * Detects whether this value represents a JSON number.
     *
     * @return `true` if this value represents a JSON number
     */
    open val isNumber: Boolean
        get() = false

    /*
     * Detects whether this value represents a JSON string.
     *
     * @return `true` if this value represents a JSON string
     */
    open val isString: Boolean
        get() = false

    /*
     * Detects whether this value represents a boolean value.
     *
     * @return `true` if this value represents either the JSON literal `true` or
     * `false`
     */
    open val isBoolean: Boolean
        get() = false

    /*
     * Detects whether this value represents the JSON literal `true`.
     *
     * @return `true` if this value represents the JSON literal `true`
     */
    open val isTrue: Boolean
        get() = false

    /*
     * Detects whether this value represents the JSON literal `false`.
     *
     * @return `true` if this value represents the JSON literal `false`
     */
    open val isFalse: Boolean
        get() = false

    /*
     * Detects whether this value represents the JSON literal `null`.
     *
     * @return `true` if this value represents the JSON literal `null`
     */
    open val isNull: Boolean
        get() = false

    /*
     * Returns this JSON value as [JsonObject], assuming that this value represents a JSON
     * object. If this is not the case, an exception is thrown.
     *
     * @return a JSONObject for this value
     * @throws UnsupportedOperationException
     * if this value is not a JSON object
     */
    open fun asObject(): JsonObject =
        throw NotImplementedError("Not an object: " + toString())

    /*
     * Returns this JSON value as [JsonArray], assuming that this value represents a JSON array.
     * If this is not the case, an exception is thrown.
     *
     * @return a JSONArray for this value
     * @throws UnsupportedOperationException
     * if this value is not a JSON array
     */
    open fun asArray(): JsonArray =
        throw NotImplementedError("Not an array: " + toString())
}