/*
 * Copyright 2023 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package microBenchmarks

import kotlinx.benchmark.*

class ChunkBuffer(var readPosition: Int, var writePosition: Int = readPosition + Random.nextInt(50)) {
    private val nextRef: AtomicRef<ChunkBuffer?> = atomic(null)

    /*
     * Reference to next buffer view. Useful to chain multiple views.
     * @see appendNext
     * @see cleanNext
     */
    var next: ChunkBuffer? get() = nextRef.value
        set(newValue) {
            if (newValue == null) {
                cleanNext()
            } else {
                appendNext(newValue)
            }
        }

    fun cleanNext(): ChunkBuffer? {
        return nextRef.getAndSet(null)
    }

    private fun appendNext(chunk: ChunkBuffer) {
        if (!nextRef.compareAndSet(null, chunk)) {
            throw IllegalStateException("This chunk has already a next chunk.")
        }
    }

    inline val readRemaining: Int get() = writePosition - readPosition
}

fun ChunkBuffer.remainingAll(): Long = remainingAll(0L)

private tailrec fun ChunkBuffer.remainingAll(n: Long): Long {
    val rem = readRemaining.toLong() + n
    val next = this.next ?: return rem
    return next.remainingAll(rem)
}

class LinkedListOfBuffers(var head: ChunkBuffer = ChunkBuffer(0,0),
                          var remaining: Long = head.remainingAll()) {
     var tailRemaining: Long = remaining - head.readRemaining
        set(newValue) {
            if (newValue < 0) {
                error("tailRemaining is negative: $newValue")
            }
            val tailSize = head.next?.remainingAll() ?: 0L
            if (newValue == 0L) {
                if (tailSize != 0L) {
                    error("tailRemaining is set 0 while there is a tail of size $tailSize")
                }
            }

            field = newValue
        }
}

@State(Scope.Benchmark)
class LinkedListWithAtomicsBenchmark {
    lateinit var list: LinkedListOfBuffers

    @Setup
    fun setup() {
        val chunks: MutableList<ChunkBuffer> = ArrayList()
        @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        (0..BENCHMARK_SIZE/2).forEachIndexed { index, i ->
            val chunk = ChunkBuffer(Random.nextInt())
            chunks.add(chunk)
            if (i > 0)
                chunks[i - 1].next = chunk
        }
        list = LinkedListOfBuffers(chunks[0])
    }

    @Benchmark
    tailrec fun ensureNext(current: ChunkBuffer = list.head): ChunkBuffer? {
        return when (val next = current.next) {
            null -> null
            else -> {
                list.tailRemaining = Random.nextInt().toLong() + 1
                ensureNext(next)
            }
        }
    }
}

