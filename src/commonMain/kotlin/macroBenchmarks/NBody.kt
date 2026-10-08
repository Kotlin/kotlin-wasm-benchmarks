/* The Computer Language Benchmarks Game
 * http://shootout.alioth.debian.org/
 *
 * Based on nbody.java and adapted basde on the SOM version.
 */

package macroBenchmarks

import macroBenchmarks.nbody.NBodySystem
import kotlin.collections.List
import kotlinx.benchmark.Blackhole

class NBody : MacroBenchmark() {
    override fun innerBenchmarkLoop(innerIterations: Int, blackhole: Blackhole): Boolean {
        val system = NBodySystem()
        for (i in 0 until innerIterations) {
            system.advance(0.01)
        }
        val result = system.energy()
        blackhole.consume(result)
        return verifyResult(result, innerIterations)
    }

    override val defaultInnerIterations: List<Int> = listOf(1, 250000)

    private fun verifyResult(result: Double, innerIterations: Int): Boolean {
        return when (innerIterations) {
            250000 -> result == -0.1690859889909308
            1 -> result == -0.16907495402506745
            else -> false
        }
    }
}