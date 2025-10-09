package microBenchmarks

import kotlinx.benchmark.*

@State(Scope.Benchmark)
class WhenBenchmark {

    private lateinit var integersDense: IntArray
    private lateinit var integersSparse: IntArray
    private lateinit var shortsDense: ShortArray
    private lateinit var shortsSparse: ShortArray
    private lateinit var bytesDense: ByteArray
    private lateinit var bytesSparse: ByteArray
    private lateinit var charsDense: CharArray
    private lateinit var charsSparse: CharArray
    private lateinit var strings: Array<String>

    private var floatConst: Float = 0F
    private var doubleConst: Double = 0.0

    private lateinit var floats: FloatArray
    private lateinit var doubles: DoubleArray

    private lateinit var integersDataDense: IntArray
    private lateinit var integersDataSparse: IntArray
    private lateinit var shortsDataDense: ShortArray
    private lateinit var shortsDataSparse: ShortArray
    private lateinit var bytesDataDense: ByteArray
    private lateinit var bytesDataSparse: ByteArray
    private lateinit var charsDataDense: CharArray
    private lateinit var charsDataSparse: CharArray
    private lateinit var stringsData: Array<String>
    private lateinit var floatsData: FloatArray
    private lateinit var doublesData: DoubleArray

    @Setup
    fun setup() {

        integersDense = intArrayOf(100500, 100502, 100504, 100506)
        integersSparse = intArrayOf(100500, 100505, 100510, 100512)
        shortsDense = shortArrayOf(10500, 10502, 10504, 10506)
        shortsSparse = shortArrayOf(10500, 10505, 10510, 10512)
        bytesDense = byteArrayOf(100, 102, 104, 106)
        bytesSparse = byteArrayOf(100, 105, 110, 112)
        charsDense = charArrayOf('a', 'b', 'c', 'e', 'f', 'h')
        charsSparse = charArrayOf('a', 'b', 'f', 'k', 't', 'z')
        strings = arrayOf("aa", "bk", "fg", "eg")

        floatConst = 1.123F
        doubleConst = 1.123456789123456

        floats = FloatArray(10) { (it + 1) * floatConst }
        doubles = DoubleArray(10) { (it + 1) * doubleConst }

        val size = BENCHMARK_SIZE * 10

        integersDataDense = IntArray(size) { i -> integersDense[i % integersDense.size] }
        integersDataSparse = IntArray(size) { i -> integersSparse[i % integersSparse.size] }
        charsDataDense = CharArray(size) { i -> charsDense[i % charsDense.size] }
        charsDataSparse = CharArray(size) { i -> charsSparse[i % charsSparse.size] }
        shortsDataDense = ShortArray(size) { i -> shortsDense[i % shortsDense.size] }
        shortsDataSparse = ShortArray(size) { i -> shortsSparse[i % shortsSparse.size] }
        bytesDataDense = ByteArray(size) { i -> bytesDense[i % bytesDense.size] }
        bytesDataSparse = ByteArray(size) { i -> bytesSparse[i % bytesSparse.size] }
        stringsData = Array(size) { i -> strings[i % strings.size] }
        floatsData = FloatArray(size) { i -> floats[i % floats.size] }
        doublesData = DoubleArray(size) { i -> doubles[i % doubles.size] }
    }

    @Benchmark
    fun charWhenDense(): Int {
        var sum = 0
        val data = charsDataDense
        for (i in 0 until data.size) {
            val char = data[i]
            sum += when (char) {
                'a' -> 13
                'b' -> 91
                'c' -> 34
                'e' -> 231
                'h' -> 233
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun charWhenSparse(): Int {
        var sum = 0
        val data = charsDataSparse
        for (i in 0 until data.size) {
            val char = data[i]
            sum += when (char) {
                'a' -> 13
                'f' -> 34
                'k' -> 91
                't' -> 231
                'z' -> 233
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun intWhenDense(): Int {
        var sum = 0
        val data = integersDataDense
        for (i in 0 until data.size) {
            val v = data[i]
            sum += when (v) {
                100500 -> 13
                100502 -> 91
                100504 -> 34
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun intWhenSparse(): Int {
        var sum = 0
        val data = integersDataSparse
        for (i in 0 until data.size) {
            val v = data[i]
            sum += when (v) {
                100500 -> 13
                100505 -> 91
                100510 -> 34
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun shortWhenDense(): Int {
        var sum = 0
        val data = shortsDataDense
        for (i in 0 until data.size) {
            val v = data[i]
            sum += when (v) {
                10500.toShort() -> 13
                10502.toShort() -> 91
                10504.toShort() -> 34
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun shortWhenSparse(): Int {
        var sum = 0
        val data = shortsDataSparse
        for (i in 0 until data.size) {
            val v = data[i]
            sum += when (v) {
                10500.toShort() -> 13
                10505.toShort() -> 91
                10510.toShort() -> 34
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun byteWhenDense(): Int {
        var sum = 0
        val data = bytesDataDense
        for (i in 0 until data.size) {
            val v = data[i]
            sum += when (v) {
                100.toByte() -> 13
                102.toByte() -> 91
                104.toByte() -> 34
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun byteWhenSparse(): Int {
        var sum = 0
        val data = bytesDataSparse
        for (i in 0 until data.size) {
            val v = data[i]
            sum += when (v) {
                100.toByte() -> 13
                105.toByte() -> 91
                110.toByte() -> 34
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun stringWhen(): Int {
        var sum = 0
        val data = stringsData
        for (i in 0 until data.size) {
            val s = data[i]
            sum += when (s) {
                "aa" -> 13
                "bk" -> 91
                "fg" -> 34
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun floatWhen(): Int {
        var sum = 0
        val data = floatsData
        for (i in 0 until data.size) {
            val v = data[i]
            sum += when (v) {
                1.123F -> 13
                3.369F -> 91
                4.492F -> 34
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun doubleWhen(): Int {
        var sum = 0
        val data = doublesData
        for (i in 0 until data.size) {
            val v = data[i]
            sum += when (v) {
                1.123 -> 13
                3.369 -> 91
                4.492 -> 34
                else -> 29
            }
        }
        return sum
    }
}