import java.io.File
import kotlin.sequences.flatMap
import kotlin.system.exitProcess

fun exit() {
    println("arg1 is folder with csv's")
    exitProcess(1)
}

fun parseCsv(csvFile: File): List<Pair<String, Double>> {
    return csvFile.readLines()
        .filterNot { it.startsWith("\"Benchmark\"") }
        .map { it.split(",") }
        .map { it[0] to it[4].toDouble() }
}

fun parseJson(jsonFile: File): List<Pair<String, Double>> {
    val singleLineJson = jsonFile.readText().replace("\n", "")

    val benchmarks = Regex(".+?(\"benchmark\".+?scoreError)+.+?").findAll(singleLineJson).map { it.groupValues[1] }.toList()

    val benchmarkRegex = Regex("\"benchmark\"\\s:\\s(\".+?\").+?\"score\":\\s(.+?),.+")
    val benchmarkValues = benchmarks.map {
        benchmarkRegex.find(it)!!.groupValues
    }

    return benchmarkValues.map {
        it[1] to it[2].toDouble()
    }
}


fun main(args: Array<String>) {
    if (args.isEmpty()) exit()
    val directory = File(args[0])
    if (!directory.exists()) exit()

    val values = directory
        .walk()
        .filter { it.extension == "json" } //csv
        .flatMap { file -> parseJson(file) } //parseCsv

    values
        .groupBy({ it.first}, { it.second })
        .map { it.key to (it.value.minOf { x -> x } to it.value.maxOf { x -> x })}
        .forEach {
            println("ChartData(${it.first}, ${it.second.first}, ${it.second.second}),")
        }
}

main(arrayOf("/Users/Igor.Yakovlev/Downloads/WASM_Main_101_artifacts/reports"))