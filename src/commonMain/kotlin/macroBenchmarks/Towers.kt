/* This code is based on the SOM class library.
 *
 * Copyright (c) 2001-2016 see AUTHORS.md file
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the 'Software'), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package macroBenchmarks

class Towers : MacroBenchmark() {
    override fun benchmark(): Any =
        TowersImplementation().run()

    override fun verifyResult(result: Any): Boolean =
        8191 == result as Int

    private class TowersImplementation {
        private class TowersDisk constructor(val size: Int) {
            var next: TowersDisk? = null
        }

        fun run(): Int {
            buildTowerAt(0, 13)
            movesDone = 0
            moveDisks(13, 0, 1)
            return movesDone
        }

        private val piles: Array<TowersDisk?> = arrayOfNulls(3)
        private var movesDone = 0
        private fun pushDisk(disk: TowersDisk, pile: Int) {
            val top = piles[pile]
            check (!(top != null && (disk.size >= top.size))) {
                "Cannot put a big disk on a smaller one"
            }
            disk.next = top
            piles[pile] = disk
        }

        private fun popDiskFrom(pile: Int): TowersDisk {
            val top = piles[pile] ?: error("Attempting to remove a disk from an empty pile")
            piles[pile] = top.next
            top.next = null
            return top
        }

        private fun moveTopDisk(fromPile: Int, toPile: Int) {
            pushDisk(popDiskFrom(fromPile), toPile)
            movesDone++
        }

        private fun buildTowerAt(pile: Int, disks: Int) {
            for (i in disks downTo 0) {
                pushDisk(TowersDisk(i), pile)
            }
        }

        private fun moveDisks(disks: Int, fromPile: Int, toPile: Int) {
            if (disks == 1) {
                moveTopDisk(fromPile, toPile)
            } else {
                val otherPile = 3 - fromPile - toPile
                moveDisks(disks - 1, fromPile, otherPile)
                moveTopDisk(fromPile, toPile)
                moveDisks(disks - 1, otherPile, toPile)
            }
        }
    }
}