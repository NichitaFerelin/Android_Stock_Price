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

package com.ferelin.stockprice.ui.aboutSection.news

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.base.ViewAnimatorScrollable
import com.ferelin.stockprice.databinding.FragmentNewsBinding
import com.ferelin.stockprice.ui.aboutSection.news.adapter.NewsItemDecoration
import com.ferelin.stockprice.ui.aboutSection.news.adapter.NewsRecyclerAdapter
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.utils.showDefaultDialog

class NewsViewController : BaseViewController<ViewAnimatorScrollable, FragmentNewsBinding>() {

    override val mViewAnimator = ViewAnimatorScrollable()

    override fun onViewCreated(savedInstanceState: Bundle?, fragment: Fragment) {
        super.onViewCreated(savedInstanceState, fragment)
        setUpRecyclerView()
    }

    override fun onDestroyView() {
        postponeReferencesRemove {
            viewBinding.recyclerViewNews.adapter = null
            super.onDestroyView()
        }
    }

    fun setArgumentsViewDependsOn(newsAdapter: NewsRecyclerAdapter) {
        viewBinding.recyclerViewNews.adapter = newsAdapter
    }

    fun onNewsUrlClicked(company: AdaptiveCompany, position: Int) {
        val url = company.companyNews.browserUrls[position]
        val isNavigated = mNavigator?.navigateToUrl(context, url)
        if (isNavigated == false) {
            showDefaultDialog(context, context.getString(R.string.errorNoAppToOpenUrl))
        }
    }

    fun onDataLoadingStateChanged(isDataLoading: Boolean) {
        viewBinding.progressBar.isVisible = isDataLoading
    }

    fun onFabClicked() {
        scrollToTop()
    }

    fun onError() {
        viewBinding.progressBar.isVisible = false
    }

    private fun setUpRecyclerView() {
        viewBinding.recyclerViewNews.addItemDecoration(NewsItemDecoration(viewBinding.root.context))
    }

    private fun scrollToTop() {
        val fabScaleOutCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                viewBinding.fab.visibility = View.INVISIBLE
                viewBinding.fab.scaleX = 1.0F
                viewBinding.fab.scaleY = 1.0F
            }
        }
        val recyclerViewManager = viewBinding.recyclerViewNews.layoutManager

        /**
         * Start scrolling to top -> recycler view fade out -> hard scroll to position 7
         * -> recycler view fade in -> smooth scroll to 0
         * */
        if (recyclerViewManager is LinearLayoutManager &&
            recyclerViewManager.findFirstVisibleItemPosition() > 12
        ) {
            val fadeInCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    viewBinding.recyclerViewNews.visibility = View.VISIBLE
                    viewBinding.recyclerViewNews.smoothScrollToPosition(0)
                    mViewAnimator.runScaleOutAnimation(viewBinding.fab, fabScaleOutCallback)
                }
            }
            val fadeOutCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    viewBinding.recyclerViewNews.smoothScrollBy(
                        0,
                        -viewBinding.recyclerViewNews.height
                    )
                }

                override fun onAnimationEnd(animation: Animation?) {
                    viewBinding.recyclerViewNews.visibility = View.GONE
                    viewBinding.recyclerViewNews.scrollToPosition(7)
                    mViewAnimator.runFadeInAnimation(viewBinding.recyclerViewNews, fadeInCallback)
                }
            }
            mViewAnimator.runFadeOutAnimation(viewBinding.recyclerViewNews, fadeOutCallback)
        } else {
            mViewAnimator.runScaleOutAnimation(viewBinding.fab, fabScaleOutCallback)
            viewBinding.recyclerViewNews.smoothScrollToPosition(0)
        }
    }
}