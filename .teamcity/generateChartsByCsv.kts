import java.io.File
import kotlin.system.exitProcess

fun exit() {
    println("arg1 is folder with csv's")
    exitProcess(1)
}

fun main(args: Array<String>) {
    if (args.isEmpty()) exit()
    val directory = File(args[0])
    if (!directory.exists()) exit()

    directory
        .walk()
        .filter { it.extension == "csv" }
        .flatMap { it.readLines() }
        .filterNot { it.startsWith("\"Benchmark\"") }
        .map { it.split(",") }
        .map { it[0] to it[4].toDouble() }
        .groupBy({ it.first}, { it.second })
        .map { it.key to (it.value.minOf { x -> x } to it.value.maxOf { x -> x })}
        .forEach {
            println("ChartData(${it.first}, ${it.second.first}, ${it.second.second}),")
        }
}

main(arrayOf("/Users/Igor.Yakovlev/Downloads/WASM_Main_101_artifacts/reports"))