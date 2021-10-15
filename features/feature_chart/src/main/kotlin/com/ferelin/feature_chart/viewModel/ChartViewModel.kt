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

package com.ferelin.feature_chart.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.params.ChartParams
import com.ferelin.core.resolvers.NetworkResolver
import com.ferelin.core.utils.SHARING_STOP_TIMEOUT
import com.ferelin.core.utils.ifNotEmpty
import com.ferelin.core.view.chart.ChartPastPrices
import com.ferelin.core.view.chart.points.Marker
import com.ferelin.domain.entities.PastPrice
import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.interactors.PastPriceInteractor
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.feature_chart.mapper.PastPriceTypeMapper
import com.ferelin.feature_chart.viewData.ChartViewMode
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import com.ferelin.shared.NetworkListener
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias ChartPastPriceState = LoadState<ChartPastPrices>

class ChartViewModel @Inject constructor(
    stockPriceInteractor: StockPriceInteractor,
    private val mPastPriceInteractor: PastPriceInteractor,
    private val mPastPriceTypeMapper: PastPriceTypeMapper,
    private val mNetworkResolver: NetworkResolver,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel(), NetworkListener {

    private var mPastPrices: List<PastPrice> = emptyList()

    private val mPastPriceLoad = MutableStateFlow<ChartPastPriceState>(LoadState.None())
    val pastPriceLoadState: StateFlow<ChartPastPriceState>
        get() = mPastPriceLoad.asStateFlow()

    val isNetworkAvailable: Boolean
        get() = mNetworkResolver.isNetworkAvailable

    var chartParams = ChartParams()
    var selectedMarker: Marker? = null

    val actualStockPrice: SharedFlow<StockPrice> = stockPriceInteractor
        .observeActualStockPriceResponses()
        .filter { it is LoadState.Prepared && it.data.id == chartParams.companyId }
        .map { (it as LoadState.Prepared).data }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    var chartMode: ChartViewMode = ChartViewMode.All

    init {
        mNetworkResolver.registerNetworkListener(this)
    }

    override suspend fun onNetworkAvailable() {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mPastPriceLoad.value.ifPrepared {
                loadFromNetwork()
            } ?: loadPastPrices()
        }
    }

    override suspend fun onNetworkLost() {
        // Do nothing
    }

    override fun onCleared() {
        mNetworkResolver.unregisterNetworkListener(this)
        super.onCleared()
    }

    fun loadPastPrices() {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mPastPriceLoad.value = LoadState.Loading()

            loadFromDb()
            loadFromNetwork()
        }
    }

    fun onNewChartMode(chartViewMode: ChartViewMode) {
        this.chartMode = chartViewMode
        onNewPastPrices(mPastPrices)
    }

    private suspend fun loadFromDb() {
        mPastPriceInteractor
            .getAllPastPrices(chartParams.companyId)
            .ifNotEmpty { dbPrices -> onNewPastPrices(dbPrices) }
    }

    private suspend fun loadFromNetwork() {
        mPastPriceInteractor
            .loadPastPrices(chartParams.companyId, chartParams.companyTicker)
            .let { responseState ->
                if (responseState is LoadState.Prepared) {
                    onNewPastPrices(responseState.data)
                } else if (mPastPriceLoad.value !is LoadState.Prepared) {
                    mPastPriceLoad.value = LoadState.Error()
                }
            }
    }

    private fun onNewPastPrices(pastPrices: List<PastPrice>) {
        mPastPrices = pastPrices

        mPastPriceTypeMapper
            .mapByViewMode(chartMode, pastPrices)
            ?.let { chartPastPrices ->
                mPastPriceLoad.value = LoadState.Prepared(chartPastPrices)
            }
    }
}