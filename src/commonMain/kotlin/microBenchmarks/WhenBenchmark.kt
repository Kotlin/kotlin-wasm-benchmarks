package microBenchmarks

import kotlinx.benchmark.*

@State(Scope.Benchmark)
class WhenBenchmark {

    private lateinit var integers: List<Int>
    private lateinit var shorts: List<Short>
    private lateinit var bytes: List<Byte>
    private lateinit var chars: List<Char>
    private lateinit var strings: List<String>

    private var floatConst: Float = 0F
    private var doubleConst: Double = 0.0

    private lateinit var floats: List<Float>
    private lateinit var doubles: List<Double>

    private val integersData = mutableListOf<Int>()
    private val shortsData = mutableListOf<Short>()
    private val bytesData = mutableListOf<Byte>()
    private val charsData = mutableListOf<Char>()
    private val stringsData = mutableListOf<String>()
    private val floatsData = mutableListOf<Float>()
    private val doublesData = mutableListOf<Double>()

    @Setup
    fun setup() {

        integers = (100500..100510).toList()
        shorts = (10500..10510).map { it.toShort() }
        bytes = (100..110).map { it.toByte() }
        chars = ('a'..'k').toList()
        strings = ('a' .. 'k').flatMap { a -> ('a' .. 'k').map { b -> "$a$b" } }

        floatConst = 1.123F
        doubleConst = 1.123456789123456

        floats = (1..10).map { it * floatConst }
        doubles = (1..10).map { it * doubleConst }

        for (i in 1..BENCHMARK_SIZE) {
            charsData.add(chars[i % chars.size])
            shortsData.add(shorts[i % shorts.size])
            bytesData.add(bytes[i % bytes.size])
            integersData.add(integers[i % integers.size])
            stringsData.add(strings[i % strings.size])
            floatsData.add(floats[i % floats.size])
            doublesData.add(doubles[i % doubles.size])
        }
    }

    @Benchmark
    fun charWhenDense(): Int {
        var sum = 0
        for (char in charsData) {
            sum += when(char) {
                'a' -> 13
                'c' -> 91
                'e' -> 34
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun charWhenSparse(): Int {
        var sum = 0
        for (char in charsData) {
            sum += when(char) {
                'a' -> 13
                'f' -> 34
                'k' -> 91
                else -> 29
            }
        }
        return sum
    }

    @Benchmark
    fun intWhenDense(): Int {
        var sum = 0
        for (i in integersData) {
            sum += when(i) {
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
        for (int in integersData) {
            sum += when(int) {
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
        for (short in shortsData) {
            sum += when(short) {
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
        for (short in shortsData) {
            sum += when(short) {
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
        for (byte in bytesData) {
            sum += when(byte) {
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
        for (byte in bytesData) {
            sum += when(byte) {
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
        for (string in stringsData) {
            sum += when(string) {
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
        for (float in floatsData) {
            sum += when(float) {
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
        for (double in doublesData) {
            sum += when(double) {
                1.123 -> 13
                3.369 -> 91
                4.492 -> 34
                else -> 29
            }
        }
        return sum
    }
}