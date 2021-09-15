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

package com.ferelin.stockprice.ui.stocksSection.base

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.ui.stocksSection.common.adapter.StockClickListener
import com.ferelin.stockprice.ui.stocksSection.common.adapter.StockViewHolder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [BaseStocksFragment] provides base logic of loading companies updates
 */
abstract class BaseStocksFragment<
        ViewBindingType : ViewBinding,
        ViewModelType : BaseStocksViewModel,
        ViewControllerType : BaseStocksViewController<ViewBindingType>>
    : BaseFragment<ViewBindingType, ViewModelType, ViewControllerType>(), StockClickListener {

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mViewModel.stocksRecyclerAdapter.setOnStockCLickListener(this)
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            collectEventCompanyChanged()
        }
    }

    override fun onStockClicked(stockViewHolder: StockViewHolder, company: AdaptiveCompany) {
        mViewController.onStockClicked(company)
    }

    override fun onFavouriteIconClicked(company: AdaptiveCompany) {
        mViewModel.onFavouriteIconClicked(company)
    }

    override fun onHolderRebound(stockViewHolder: StockViewHolder) {
        mViewController.onStockHolderRebound(stockViewHolder)
    }

    override fun onHolderUntouched(stockViewHolder: StockViewHolder, rebounded: Boolean) {
        mViewController.onStockHolderUntouched(
            stockViewHolder,
            rebounded,
            onAccepted = { mViewModel.onFavouriteIconClicked(it) }
        )
    }

    fun onFabClicked() {
        mViewController.onFabClicked()
    }

    private suspend fun collectEventCompanyChanged() {
        mViewModel.eventCompanyChanged.collect { notificator ->
            withContext(mCoroutineContext.Main) {
                mViewController.onCompanyChanged(notificator)
            }
        }
    }
}