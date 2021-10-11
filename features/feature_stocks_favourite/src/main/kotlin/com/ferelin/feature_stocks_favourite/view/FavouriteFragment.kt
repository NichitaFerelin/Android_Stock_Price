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

package com.ferelin.feature_stocks_favourite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ferelin.core.view.BaseStocksFragment
import com.ferelin.core.viewModel.StocksMode
import com.ferelin.feature_stocks_favourite.databinding.FragmentFavouriteBinding
import com.ferelin.feature_stocks_favourite.viewModel.FavouriteViewModel

class FavouriteFragment : BaseStocksFragment<FragmentFavouriteBinding, FavouriteViewModel>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFavouriteBinding
        get() = FragmentFavouriteBinding::inflate

    override val mStocksMode = StocksMode.ONLY_FAVOURITES

    override val mViewModel: FavouriteViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        stocksRecyclerView = mViewBinding.recyclerViewStocks
        // fab = mViewBinding.fab
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        fun newInstance(data: Any?): FavouriteFragment {
            return FavouriteFragment()
        }
    }
}