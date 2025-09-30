package microBenchmarks

import kotlinx.benchmark.*

@State(Scope.Benchmark)
class WhenBenchmark {

    private val integers = 100500..100510
    private val shorts = (10500..10510).map { it.toShort() }
    private val bytes = (100..110).map { it.toByte() }
    private val chars = 'a'..'k'
    private val strings = ('a' .. 'k').flatMap {a -> ('a' .. 'k').map { b -> "$a$b" } }

    private val floatConst = 1.123.toFloat()
    private val doubleConst = 1.123456789123456

    private val floats = (1..10).map { it * floatConst }
    private val doubles = (1..10).map { it * doubleConst }

    private val integersData = mutableListOf<Int>()
    private val shortsData = mutableListOf<Short>()
    private val bytesData = mutableListOf<Byte>()
    private val charsData = mutableListOf<Char>()
    private val stringsData = mutableListOf<String>()
    private val floatsData = mutableListOf<Float>()
    private val doublesData = mutableListOf<Double>()

    @Setup
    fun setup() {
        for (i in 1..BENCHMARK_SIZE) {
            charsData.add(chars.random())
            shortsData.add(shorts.random())
            bytesData.add(bytes.random())
            integersData.add(integers.random())
            stringsData.add(strings.random())
            floatsData.add(floats.random())
            doublesData.add(doubles.random())
        }
    }

    @Benchmark
    fun charWhenDense(): Int {
        var sum = 0
        for (char in charsData) {
            when(char) {
                'a' -> sum += 13
                'c' -> sum += 91
                'e' -> sum += 34
                else -> sum += 29
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
                floatConst -> 13
                floatConst * 3 -> 91
                floatConst * 4 -> 34
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
                doubleConst -> 13
                doubleConst * 3 -> 91
                doubleConst * 4 -> 34
                else -> 29
            }
        }
        return sum
    }
}