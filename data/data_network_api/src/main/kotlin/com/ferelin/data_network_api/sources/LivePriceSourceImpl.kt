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

package com.ferelin.data_network_api.sources

import com.ferelin.domain.entities.LiveTimePrice
import com.ferelin.domain.sources.LivePriceSource
import com.ferelin.data_network_api.mappers.LivePriceMapper
import com.ferelin.data_network_api.resolvers.LivePriceSocketResolver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class LivePriceSourceImpl @Inject constructor(
    private val livePriceSocketResolver: LivePriceSocketResolver,
    private val livePriceMapper: LivePriceMapper,
) : LivePriceSource {

    override fun observeLiveTimePriceUpdates(): Flow<LiveTimePrice?> {
        Timber.d("observe live time price updates")

        return livePriceSocketResolver
            .openConnection()
            .map(livePriceMapper::map)
    }

    override suspend fun cancelLiveTimeUpdates() {
        Timber.d("cancel live time updates")
        livePriceSocketResolver.closeConnection()
    }

    override suspend fun subscribeCompanyOnUpdates(companyTicker: String) {
        livePriceSocketResolver.subscribe(companyTicker)
    }

    override suspend fun unsubscribeCompanyFromUpdates(companyTicker: String) {
        livePriceSocketResolver.unsubscribe(companyTicker)
    }
}