package com.ferelin.stockprice.ui.stocksSection.search

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

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.animation.Animation
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.FragmentSearchBinding
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewController
import com.ferelin.stockprice.ui.stocksSection.common.StocksRecyclerAdapter
import com.ferelin.stockprice.ui.stocksSection.search.itemDecoration.SearchItemDecoration
import com.ferelin.stockprice.ui.stocksSection.search.itemDecoration.SearchItemDecorationLandscape
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.anim.MotionManager
import com.ferelin.stockprice.utils.hideKeyboard
import com.ferelin.stockprice.utils.openKeyboard
import com.ferelin.stockprice.utils.showToast
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewController : BaseStocksViewController<FragmentSearchBinding>() {

    override val mViewAnimator: SearchViewAnimatorScrollable = SearchViewAnimatorScrollable()

    override val mStocksRecyclerView: RecyclerView
        get() = viewBinding!!.recyclerViewSearchResults

    override fun onCreateFragment(fragment: Fragment) {
        super.onCreateFragment(fragment)
        fragment.sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onViewCreated(
        savedInstanceState: Bundle?,
        fragment: Fragment,
        viewLifecycleScope: LifecycleCoroutineScope
    ) {
        super.onViewCreated(savedInstanceState, fragment, viewLifecycleScope)
        setUpRecyclerViews()
        if (savedInstanceState == null) {
            setFocus()
        }
    }

    override fun onDestroyView() {
        postponeReferencesRemove {
            mStocksRecyclerView.adapter = null
            viewBinding!!.recyclerViewSearchedHistory.adapter = null
            viewBinding!!.recyclerViewPopularRequests.adapter = null
            super.onDestroyView()
        }
    }

    fun setArgumentsViewDependsOn(
        stocksRecyclerAdapter: StocksRecyclerAdapter,
        searchesHistoryRecyclerAdapter: SearchRequestsAdapter,
        popularSearchesRecyclerAdapter: SearchRequestsAdapter,
        savedViewTransitionState: Int,
        fragmentManager: FragmentManager
    ) {
        restoreTransitionState(savedViewTransitionState)

        searchesHistoryRecyclerAdapter.setOnTickerClickListener { item, _ ->
            onSearchTickerClicked(item)
        }
        popularSearchesRecyclerAdapter.setOnTickerClickListener { item, _ ->
            onSearchTickerClicked(item)
        }
        stocksRecyclerAdapter.setHeader(mContext!!.resources.getString(R.string.hintStocks))
        mStocksRecyclerView.adapter = stocksRecyclerAdapter
        viewBinding!!.recyclerViewSearchedHistory.adapter = searchesHistoryRecyclerAdapter
        viewBinding!!.recyclerViewPopularRequests.adapter = popularSearchesRecyclerAdapter

        super.fragmentManager = fragmentManager
    }

    /**
     * @return motion layout transition state to save it in view model
     */
    fun onSearchStocksResultChanged(results: ArrayList<AdaptiveCompany>?): Int {
        return if (results == null || results.isEmpty()) {
            viewBinding!!.root.transitionToStart()
            0
        } else {
            (mStocksRecyclerView.adapter as StocksRecyclerAdapter).setCompanies(results)
            viewBinding!!.root.transitionToEnd()
            1
        }
    }

    fun onSearchTextChanged(text: String) {
        if (text.isEmpty()) {
            hideCloseIcon()
        } else showCloseIcon()
    }

    fun onSearchRequestsChanged(notificator: DataNotificator<ArrayList<AdaptiveSearchRequest>>) {
        val data = notificator.data!!
        val adapter = viewBinding!!.recyclerViewSearchedHistory.adapter as SearchRequestsAdapter
        adapter.setData(data)
    }

    fun onPopularSearchRequestsChanged(results: ArrayList<AdaptiveSearchRequest>) {
        val adapter = viewBinding!!.recyclerViewPopularRequests.adapter as SearchRequestsAdapter
        adapter.setData(results)
    }

    fun onBackButtonClicked(ifNotHandled: () -> Unit) {
        if (viewBinding!!.root.progress == 0F) {
            ifNotHandled.invoke()
            return
        }

        viewBinding!!.root.apply {
            addTransitionListener(object : MotionManager() {
                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    ifNotHandled.invoke()
                }
            })
            transitionToStart()
        }
    }

    fun handleOnBackPressed(lastSearchRequest: String): Boolean {
        if (lastSearchRequest.isEmpty()) {
            hideKeyboard(mContext!!, viewBinding!!.root)
            return false
        }

        viewBinding!!.editTextSearch.setText("")
        return true
    }

    fun onCloseIconClicked() {
        viewBinding!!.editTextSearch.setText("")
    }

    fun onError(message: String) {
        showToast(mContext!!, message)
    }

    fun onStop() {
        hideKeyboard(mContext!!, viewBinding!!.root)
    }

    private fun setFocus() {
        mViewLifecycleScope!!.launch(mCoroutineContext.IO) {
            // Wait animation
            delay(200)
            withContext(mCoroutineContext.Main) {
                viewBinding!!.editTextSearch.requestFocus()
                openKeyboard(mContext!!, viewBinding!!.editTextSearch)
            }
        }
    }

    private fun setUpRecyclerViews() {
        viewBinding!!.recyclerViewSearchedHistory.addItemDecoration(provideItemDecoration())
        viewBinding!!.recyclerViewPopularRequests.addItemDecoration(provideItemDecoration())
    }

    private fun provideItemDecoration(): SearchItemDecoration {
        return if (mContext!!.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            SearchItemDecorationLandscape(mContext!!)
        } else SearchItemDecoration(mContext!!)
    }

    private fun onSearchTickerClicked(item: AdaptiveSearchRequest) {
        viewBinding!!.editTextSearch.setText(item.searchText)
        viewBinding!!.editTextSearch.setSelection(item.searchText.length)
    }

    private fun restoreTransitionState(lastTransitionState: Int) {
        // Zero transition state is set by default
        if (lastTransitionState == 1) {
            viewBinding!!.root.progress = 1F
        }
    }

    private fun hideCloseIcon() {
        if (viewBinding!!.imageViewIconClose.scaleX == 1F) {
            mViewAnimator.runScaleOutAnimation(
                target = viewBinding!!.imageViewIconClose,
                listener = object : AnimationManager() {
                    override fun onAnimationEnd(animation: Animation?) {
                        viewBinding!!.imageViewIconClose.scaleX = 0F
                        viewBinding!!.imageViewIconClose.scaleY = 0F
                    }
                })
        }
    }

    private fun showCloseIcon() {
        if (viewBinding!!.imageViewIconClose.scaleX == 0F) {
            mViewAnimator.runScaleIn(
                target = viewBinding!!.imageViewIconClose,
                callback = object : AnimationManager() {
                    override fun onAnimationStart(animation: Animation?) {
                        viewBinding!!.imageViewIconClose.scaleX = 1F
                        viewBinding!!.imageViewIconClose.scaleY = 1F
                    }
                })
        }
    }
}