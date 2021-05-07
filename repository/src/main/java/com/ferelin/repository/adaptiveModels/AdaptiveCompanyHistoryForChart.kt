package com.ferelin.repository.adaptiveModels

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

class AdaptiveCompanyHistoryForChart(
    val price: List<Double>,
    val priceStr: List<String>,
    val dates: List<String>
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AdaptiveCompanyHistoryForChart) {
            return dates.firstOrNull() == other.dates.firstOrNull()
        } else false
    }

    override fun hashCode(): Int {
        var result = price.hashCode()
        result = 31 * result + priceStr.hashCode()
        result = 31 * result + dates.hashCode()
        return result
    }

    fun isNotEmpty() : Boolean {
        return price.isNotEmpty()
    }

    fun isEmpty() : Boolean {
        return price.isEmpty()
    }
}