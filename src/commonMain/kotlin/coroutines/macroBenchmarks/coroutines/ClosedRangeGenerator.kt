package macroBenchmarks.coroutines

class ClosedRangeGenerator(
    private val rangeStart: Int,
    private val rangeEnd: Int,
    private val step: Int
) : AbstractGenerator<Int>() {
    override suspend fun generatorBody() {
        for (i in rangeStart..rangeEnd step step) {
            yieldValue(i)
        }
    }
}
