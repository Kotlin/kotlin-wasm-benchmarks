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

const val BENCHMARK_SIZE = 10000

class AtomicRef<T>(public var value: T) {
    /*
     * Reading/writing this property maps to read/write of volatile variable.
     */

    /*
     * Maps to [AtomicReferenceFieldUpdater.lazySet].
     */
    public fun lazySet(value: T) {
        this.value = value
    }

    /*
     * Maps to [AtomicReferenceFieldUpdater.compareAndSet].
     */
    public fun compareAndSet(expect: T, update: T): Boolean {
        if (value == expect) {
            value = update
            return true
        }
        return false
    }

    /*
     * Maps to [AtomicReferenceFieldUpdater.getAndSet].
     */
    public fun getAndSet(value: T): T {
        return this.value
            .also { this.value = value }
    }
}

public fun <T> atomic(initial: T): AtomicRef<T> = AtomicRef(initial)

class Random {
    companion object {
        var seedInt = 0
        fun nextInt(boundary: Int = 100): Int {
            seedInt = (3 * seedInt + 11) % boundary
            return seedInt
        }

        var seedDouble: Double = 0.1
        fun nextDouble(boundary: Double = 100.0): Double {
            seedDouble = (7.0 * seedDouble + 7.0) % boundary
            return seedDouble
        }
    }
}