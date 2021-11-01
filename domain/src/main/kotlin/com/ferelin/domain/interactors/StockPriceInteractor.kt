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

package com.ferelin.domain.interactors

import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.repositories.StockPriceRepo
import com.ferelin.domain.sources.StockPriceSource
import com.ferelin.domain.utils.StockPriceListener
import com.ferelin.shared.LoadState
import com.ferelin.shared.NAMED_EXTERNAL_SCOPE
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * [StockPriceInteractor] allows to interact with companies stock price
 * */
@Singleton
class StockPriceInteractor @Inject constructor(
    private val stockPriceRepo: StockPriceRepo,
    private val stockPriceSource: StockPriceSource,
    private val priceListeners: List<@JvmSuppressWildcards StockPriceListener>,
    @Named(NAMED_EXTERNAL_SCOPE) private val externalScope: CoroutineScope
) {
    /**
     * Еo receive answers to requests for obtaining the current price
     * @return flow of [LoadState] with [StockPrice] if [LoadState] is successful
     * */
    fun observeActualStockPriceResponses(): Flow<LoadState<StockPrice>> {
        return stockPriceSource
            .observeActualStockPriceResponses()
            .onEach { cacheIfPrepared(it) }
    }

    /**
     * Allows to add request to load actual stock price for company
     * @param companyId is a company for which need to load stock price
     * @param companyTicker is a company ticker for which need to load stock price
     * @param keyPosition is a position which determines the "importance" of the request
     * @param isImportant if true guarantees the execution of the request
     * */
    suspend fun addRequestToGetStockPrice(
        companyId: Int,
        companyTicker: String,
        keyPosition: Int,
        isImportant: Boolean = false
    ) {
        stockPriceSource.addRequestToGetStockPrice(
            companyId,
            companyTicker,
            keyPosition,
            isImportant
        )
    }

    private fun cacheIfPrepared(loadState: LoadState<StockPrice>) {
        loadState.ifPrepared { preparedState ->
            externalScope.launch {
                stockPriceRepo.insert(preparedState.data)

                priceListeners.forEach {
                    it.onStockPriceChanged(preparedState.data)
                }
            }
        }
    }
}