/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.core.utils

import android.content.res.Resources
import android.view.View

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun <T> List<T>.ifNotEmpty(defaultValue: (data: List<T>) -> Unit) {
    if (this.isNotEmpty()) {
        defaultValue.invoke(this)
    }
}

fun View.setOnClick(listener: (() -> Unit)) {
    setOnClickListener { listener.invoke() }
}

fun Float.normalize(
    inputMin: Float,
    inputMax: Float,
    outputMin: Float,
    outputMax: Float
): Float {
    if (this < inputMin) {
        return outputMin
    } else if (this > inputMax) {
        return outputMax
    }

    return outputMin * (1 - (this - inputMin) / (inputMax - inputMin)) +
            outputMax * ((this - inputMin) / (inputMax - inputMin))
}