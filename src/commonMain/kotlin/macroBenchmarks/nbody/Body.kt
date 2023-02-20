/* The Computer Language Benchmarks Game
 * http://shootout.alioth.debian.org/
 *
 * Based on nbody.java and adapted basde on the SOM version.
 */

package macroBenchmarks.nbody

class Body(
    var x: Double, var y: Double, var z: Double,
    vx: Double, vy: Double, vz: Double, mass: Double
) {
    var vX = vx * DAYS_PER_YER
    var vY = vy * DAYS_PER_YER
    var vZ = vz * DAYS_PER_YER
    val mass: Double = mass * SOLAR_MASS

    fun offsetMomentum(px: Double, py: Double, pz: Double) {
        vX = 0.0 - px / SOLAR_MASS
        vY = 0.0 - py / SOLAR_MASS
        vZ = 0.0 - pz / SOLAR_MASS
    }

    companion object {
        private const val PI = 3.141592653589793
        private const val SOLAR_MASS = 4 * PI * PI
        private const val DAYS_PER_YER = 365.24

        @Suppress("FloatingPointLiteralPrecision")
        fun jupiter(): Body {
            return Body(
                4.84143144246472090e+00,
                -1.16032004402742839e+00,
                -1.03622044471123109e-01,
                1.66007664274403694e-03,
                7.69901118419740425e-03,
                -6.90460016972063023e-05,
                9.54791938424326609e-04
            )
        }

        @Suppress("FloatingPointLiteralPrecision")
        fun saturn(): Body {
            return Body(
                8.34336671824457987e+00,
                4.12479856412430479e+00,
                -4.03523417114321381e-01,
                -2.76742510726862411e-03,
                4.99852801234917238e-03,
                2.30417297573763929e-05,
                2.85885980666130812e-04
            )
        }

        @Suppress("FloatingPointLiteralPrecision")
        fun uranus(): Body {
            return Body(
                1.28943695621391310e+01,
                -1.51111514016986312e+01,
                -2.23307578892655734e-01,
                2.96460137564761618e-03,
                2.37847173959480950e-03,
                -2.96589568540237556e-05,
                4.36624404335156298e-05
            )
        }

        @Suppress("FloatingPointLiteralPrecision")
        fun neptune(): Body {
            return Body(
                1.53796971148509165e+01,
                -2.59193146099879641e+01,
                1.79258772950371181e-01,
                2.68067772490389322e-03,
                1.62824170038242295e-03,
                -9.51592254519715870e-05,
                5.15138902046611451e-05
            )
        }

        fun sun(): Body = Body(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0)
    }
}