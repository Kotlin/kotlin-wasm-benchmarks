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

/*
 * Created by Mikhail.Glukhikh on 06/03/2015.
 *
 * A benchmark for a single abstract method based on a string comparison
 */

@State(Scope.Benchmark)
class AbstractMethodBenchmark {

    private lateinit var arr: Array<String>
    private lateinit var sequence: String
    private lateinit var sequenceMap: HashMap<Char, Int>
    private lateinit var mutableSet: MutableSet<String>
    @Setup
    fun setup() {
        val zdf = zdf_win.let {
            it.subList(0, BENCHMARK_SIZE.coerceAtMost(it.size))
        }
        arr = zdf.toTypedArray()
        sequenceMap = HashMap()
        sequence = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        mutableSet = mutableSetOf()

        var i = 0;
        for (ch in sequence) {
            sequenceMap[ch] = i++;
        }
    }

    @Benchmark
    fun sortStrings(): Set<String> = arr.toSet()


    @Benchmark
    fun sortStringsWithComparator(): Set<String> {
        mutableSet.addAll(arr)
        return mutableSet
    }
}

