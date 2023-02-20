/* The Computer Language Benchmarks Game
 * http://shootout.alioth.debian.org/
 *
 * Based on nbody.java and adapted basde on the SOM version.
 */

package macroBenchmarks.nbody

import kotlin.math.sqrt

class NBodySystem {
    private val bodies: Array<Body> = createBodies()

    private fun createBodies(): Array<Body> {
        val bodies = arrayOf(
            Body.sun(),
            Body.jupiter(),
            Body.saturn(),
            Body.uranus(),
            Body.neptune()
        )
        var px = 0.0
        var py = 0.0
        var pz = 0.0
        for (b in bodies) {
            px += b.vX * b.mass
            py += b.vY * b.mass
            pz += b.vZ * b.mass
        }
        bodies[0].offsetMomentum(px, py, pz)
        return bodies
    }

    fun advance(dt: Double) {
        for (i in bodies.indices) {
            val iBody = bodies[i]
            for (j in i + 1 until bodies.size) {
                val jBody = bodies[j]
                val dx = iBody.x - jBody.x
                val dy = iBody.y - jBody.y
                val dz = iBody.z - jBody.z
                val dSquared = dx * dx + dy * dy + dz * dz
                val distance = sqrt(dSquared)
                val mag = dt / (dSquared * distance)
                iBody.vX = iBody.vX - dx * jBody.mass * mag
                iBody.vY = iBody.vY - dy * jBody.mass * mag
                iBody.vZ = iBody.vZ - dz * jBody.mass * mag
                jBody.vX = jBody.vX + dx * iBody.mass * mag
                jBody.vY = jBody.vY + dy * iBody.mass * mag
                jBody.vZ = jBody.vZ + dz * iBody.mass * mag
            }
        }
        for (body in bodies) {
            body.x = body.x + dt * body.vX
            body.y = body.y + dt * body.vY
            body.z = body.z + dt * body.vZ
        }
    }

    fun energy(): Double {
        var dx: Double
        var dy: Double
        var dz: Double
        var distance: Double
        var e = 0.0
        for (i in bodies.indices) {
            val iBody = bodies[i]
            e += (0.5 * iBody.mass
                    * (iBody.vX * iBody.vX + iBody.vY * iBody.vY + iBody.vZ * iBody.vZ))
            for (j in i + 1 until bodies.size) {
                val jBody = bodies[j]
                dx = iBody.x - jBody.x
                dy = iBody.y - jBody.y
                dz = iBody.z - jBody.z
                distance = sqrt(dx * dx + dy * dy + dz * dz)
                e -= iBody.mass * jBody.mass / distance
            }
        }
        return e
    }
}