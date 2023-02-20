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

/*
 * Created by semoro on 07.07.17.
 */

fun octoTest() {
    val tree = OctoTree<Boolean>(4)
    val to = (2 shl tree.depth)

    var x = 0
    @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
    var y = 0
    @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
    var z = 0

    while (x < to) {
        y = 0
        while (y < to) {
            z = 0
            while (z < to) {
                val c = (z + to * y + to * to * x) % 2 == 0

                tree.set(x, y, z, c)
                z++
            }
            y++
        }
        x++
    }

    x = 0
    @Suppress("UNUSED_VALUE")
    y = 0
    @Suppress("UNUSED_VALUE")
    z = 0
    while (x < to) {
        y = 0
        while (y < to) {
            z = 0
            while (z < to) {
                val c = (z + to * y + to * to * x) % 2 == 0

                val res = tree.get(x, y, z)

                check(res == c)
                z++
            }
            y++
        }
        x++
    }
}
