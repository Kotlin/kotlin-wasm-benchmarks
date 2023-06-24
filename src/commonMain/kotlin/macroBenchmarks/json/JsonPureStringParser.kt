/*
 * Copyright (c) 2015 Stefan Marr
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

class JsonPureStringParser(private val input: String) {
    private var index: Int = -1
    private var line: Int = 1
    private var column: Int = 0
    private var current: String? = null
    private var captureBuffer: String = ""
    private var captureStart: Int = -1

    fun parse(): JsonValue {
        read()
        skipWhiteSpace()
        val result = readValue()
        skipWhiteSpace()
        return if (isEndOfText) result else throw error("Unexpected character")
    }

    private fun readValue(): JsonValue = when (current) {
        "n" -> readNull()
        "t" -> readTrue()
        "f" -> readFalse()
        "\"" -> readString()
        "[" -> readArray()
        "{" -> readObject()
        "-", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> readNumber()
        else -> throw expected("value")
    }

    private fun readArray(): JsonArray {
        read()
        val array = JsonArray()
        skipWhiteSpace()
        if (readChar("]")) {
            return array
        }
        do {
            skipWhiteSpace()
            array.add(readValue())
            skipWhiteSpace()
        } while (readChar(","))
        if (!readChar("]")) {
            throw expected("',' or ']'")
        }
        return array
    }

    private fun readObject(): JsonObject {
        read()
        val result = JsonObject()
        skipWhiteSpace()
        if (readChar("}")) {
            return result
        }
        do {
            skipWhiteSpace()
            val name = readName()
            skipWhiteSpace()
            if (!readChar(":")) {
                throw expected("':'")
            }
            skipWhiteSpace()
            result.add(name, readValue())
            skipWhiteSpace()
        } while (readChar(","))
        if (!readChar("}")) {
            throw expected("',' or '}'")
        }
        return result
    }

    private fun readName(): String {
        if (current != "\"") {
            throw expected("name")
        }
        return readStringInternal()
    }

    private fun readNull(): JsonValue {
        read()
        readRequiredChar("u")
        readRequiredChar("l")
        readRequiredChar("l")
        return JsonLiteral.NULL
    }

    private fun readTrue(): JsonValue {
        read()
        readRequiredChar("r")
        readRequiredChar("u")
        readRequiredChar("e")
        return JsonLiteral.TRUE
    }

    private fun readFalse(): JsonValue {
        read()
        readRequiredChar("a")
        readRequiredChar("l")
        readRequiredChar("s")
        readRequiredChar("e")
        return JsonLiteral.FALSE
    }

    private fun readRequiredChar(ch: String) {
        if (!readChar(ch)) {
            throw expected("'$ch'")
        }
    }

    private fun readString(): JsonValue {
        return JsonString(readStringInternal())
    }

    private fun readStringInternal(): String {
        read()
        startCapture()
        do {
            val current1 = current // does it really help?
            if (current1 == s) break

            if (current1 == s1) {
                pauseCapture()
                readEscape()
                startCapture()
            } else {
                read()
            }
        } while (true) // don't generate br_if here
        val string = endCapture()
        read()
        return string
    }

    private fun readEscape() {
        read()
        captureBuffer += when (current) {
            "\"", "/", "\\" -> current
            "b" -> "\b"
            "f" -> "\u000c"
            "n" -> "\n"
            "r" -> "\r"
            "t" -> "\t"
            else -> throw expected("valid escape sequence")
        }
        read()
    }

    private fun readNumber(): JsonValue {
        startCapture()
        readChar("-")
        val firstDigit = current
        if (!readDigit()) {
            throw expected("digit")
        }
        if (firstDigit != "0") {
            while (readDigit()) {
            }
        }
        readFraction()
        readExponent()
        return JsonNumber(endCapture())
    }

    private fun readFraction(): Boolean {
        if (!readChar(".")) {
            return false
        }
        if (!readDigit()) {
            throw expected("digit")
        }
        while (readDigit()) {
        }
        return true
    }

    private fun readExponent(): Boolean {
        if (!readChar("e") && !readChar("E")) {
            return false
        }
        if (!readChar("+")) {
            readChar("-")
        }
        if (!readDigit()) {
            throw expected("digit")
        }

        while (readDigit()) {
        }
        return true
    }

    private fun readChar(ch: String): Boolean {
        if (current != ch) {
            return false
        }
        read()
        return true
    }

    private fun readDigit(): Boolean {
        if (!isDigit) {
            return false
        }
        read()
        return true
    }

    private fun skipWhiteSpace() {
        while (isWhiteSpace) {
            read()
        }
    }

    private fun read() {
        if (n == current) {
            line+=1
            column = 0
        }
        index+=1
        current = if (index < input.length) {
            input.substring(index, index + 1)
        } else {
            null
        }
    }

    private fun startCapture() {
        captureStart = index
    }

    private fun pauseCapture() {
        val end = if (current == null) index else index - 1
        captureBuffer += input.substring(captureStart, end + 1)
        captureStart = -1
    }

    private fun endCapture(): String {
        val end = if (current == null) index else index - 1
        val captured: String
        if (captureBuffer.isEmpty()) { //does it help?
            captured = input.substring(captureStart, end + 1)
        } else {
            captureBuffer += input.substring(captureStart, end + 1)
            captured = captureBuffer
            captureBuffer = ""
        }
        captureStart = -1
        return captured
    }

    private fun expected(expected: String): ParseException =
        error(if (isEndOfText) "Unexpected end of input" else "Expected $expected")

    private fun error(message: String): ParseException =
        ParseException(message, index, line, column - 1)

    private val isWhiteSpace: Boolean
        get() =
            when (current) {
                null -> false
                " ", "\t", "\n", "\r" -> true
                else -> false
            }
    
    private val isDigit: Boolean
        // bad IR is generated for ||?
        get() =
            when (current) {
                null -> false
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> true
                else -> false
            }
    private val isEndOfText: Boolean
        get() = current == null
}

val s = "\""
val s1 = "\\"
val n = "\n"
