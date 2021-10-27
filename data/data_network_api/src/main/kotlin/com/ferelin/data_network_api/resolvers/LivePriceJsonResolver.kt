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

package com.ferelin.data_network_api.resolvers

import com.ferelin.data_network_api.entities.LivePrice
import com.ferelin.data_network_api.entities.LivePricePojo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber
import javax.inject.Inject

class LivePriceJsonResolver @Inject constructor() {

    private val moshi by lazy {
        Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val pojoAdapter by lazy {
        moshi.adapter(LivePricePojo::class.java).lenient()
    }

    fun fromJson(jsonResponse: String): LivePrice? = try {
        Timber.d("fromJson (json response = $jsonResponse)")

        pojoAdapter.fromJson(jsonResponse)?.livePrices?.firstOrNull()
    } catch (exception: Exception) {
        Timber.d("from json (exception = $exception")
        null
    }
}