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

package com.ferelin.stockprice.ui.stocksSection.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.databinding.FragmentFavouriteBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavouriteFragment :
    BaseStocksFragment<FragmentFavouriteBinding, FavouriteViewModel, FavouriteViewController>() {

    override val mViewController: FavouriteViewController = FavouriteViewController()
    override val mViewModel: FavouriteViewModel by viewModels()

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFavouriteBinding
        get() = FragmentFavouriteBinding::inflate

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mViewController.setArgumentsViewDependsOn(
            mViewModel.stocksRecyclerAdapter,
            requireParentFragment().parentFragmentManager
        )
        mViewController.viewBinding.recyclerViewFavourites.addOnScrollListener(mOnScrollListener)
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            collectEventOnNewItem()
        }
    }

    private suspend fun collectEventOnNewItem() {
        mViewModel.eventOnNewItem.collect {
            withContext(mCoroutineContext.Main) {
                mViewController.onNewItem()
            }
        }
    }
}