package com.ferelin.stockprice.ui.stocksSection.stocksPager

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

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.transition.Fade
import androidx.viewpager2.widget.ViewPager2
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseViewController
import com.ferelin.stockprice.databinding.FragmentStocksPagerBinding
import com.ferelin.stockprice.navigation.Navigator
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.google.android.material.transition.Hold
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class StocksPagerViewController :
    BaseViewController<StocksPagerViewAnimator, FragmentStocksPagerBinding>() {

    override val mViewAnimator: StocksPagerViewAnimator = StocksPagerViewAnimator()
    private lateinit var mViewPagerChangeCallback: ViewPager2.OnPageChangeCallback

    private val mEventOnFabClicked = MutableSharedFlow<Unit>()
    val eventOnFabClicked: SharedFlow<Unit>
        get() = mEventOnFabClicked

    override fun onCreateFragment(fragment: Fragment) {
        super.onCreateFragment(fragment)
        fragment.exitTransition = Hold()
        fragment.enterTransition = Fade(Fade.IN)
    }

    override fun onViewCreated(
        savedInstanceState: Bundle?,
        fragment: Fragment,
        viewLifecycleScope: LifecycleCoroutineScope
    ) {
        super.onViewCreated(savedInstanceState, fragment, viewLifecycleScope)
        postponeTransitions(fragment)
        setUpViewPager()
    }

    override fun onDestroyView() {
        viewBinding!!.viewPager.unregisterOnPageChangeCallback(mViewPagerChangeCallback)
        super.onDestroyView()
    }

    fun setUpArgumentsViewDependsOn(viewPagerAdapter: StocksPagerAdapter, arrowState: Float) {
        viewBinding!!.viewPager.adapter = viewPagerAdapter
        viewBinding!!.bottomAppBarImageViewArrowUp.rotation = arrowState
    }

    fun onFabClicked() {
        mViewLifecycleScope!!.launch(mCoroutineContext.IO) {
            mEventOnFabClicked.emit(Unit)
        }
        hideFab()
    }

    fun onHintStocksClicked() {
        if (viewBinding!!.viewPager.currentItem != 0) {
            viewBinding!!.viewPager.setCurrentItem(0, true)
        }
    }

    fun onHintFavouriteClicked() {
        if (viewBinding!!.viewPager.currentItem != 1) {
            viewBinding!!.viewPager.setCurrentItem(1, true)
        }
    }

    fun onCardSearchClicked(fragment: Fragment) {
        navigateToSearchFragment(fragment)
    }

    fun handleOnBackPressed(): Boolean {
        if (viewBinding!!.viewPager.currentItem == 0) {
            return false
        }

        viewBinding!!.viewPager.setCurrentItem(0, true)
        return true
    }

    private fun setUpViewPager() {
        mViewPagerChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                switchTextStyles(position)
            }
        }
        viewBinding!!.viewPager.registerOnPageChangeCallback(mViewPagerChangeCallback)
    }

    private fun hideFab() {
        mViewAnimator.runScaleOut(viewBinding!!.fab, object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                viewBinding!!.fab.visibility = View.INVISIBLE
                viewBinding!!.fab.scaleX = 1.0F
                viewBinding!!.fab.scaleY = 1.0F
            }
        })
    }

    private fun navigateToSearchFragment(fragment: Fragment) {
        Navigator.navigateToSearchFragment(fragment) {
            it.addSharedElement(
                viewBinding!!.toolbar,
                mContext!!.resources.getString(R.string.transitionSearchFragment)
            )
        }
    }

    private fun applyDefaultStyle(target: TextView) {
        TextViewCompat.setTextAppearance(target, R.style.textViewH2Shadowed)
    }

    private fun applySelectedStyle(target: TextView) {
        TextViewCompat.setTextAppearance(target, R.style.textViewH1)
    }

    private fun switchTextStyles(selectedPosition: Int) {
        if (selectedPosition == 0) {
            applySelectedStyle(viewBinding!!.textViewHintStocks)
            applyDefaultStyle(viewBinding!!.textViewHintFavourite)
            mViewAnimator.runScaleInOut(viewBinding!!.textViewHintStocks)
        } else {
            applySelectedStyle(viewBinding!!.textViewHintFavourite)
            applyDefaultStyle(viewBinding!!.textViewHintStocks)
            mViewAnimator.runScaleInOut(viewBinding!!.textViewHintFavourite)
        }
    }
}