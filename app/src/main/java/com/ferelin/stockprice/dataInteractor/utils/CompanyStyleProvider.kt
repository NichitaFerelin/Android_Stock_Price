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

package com.ferelin.stockprice.dataInteractor.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import javax.inject.Inject
import javax.inject.Singleton

/**
 *  [CompanyStyleProvider] is used to change colors/images/icons dynamically.
 *  This class is responsible for providing right color/image/icon depending
 *  on some fields like: is company favourite or not, company price profit, etc.
 */
@Singleton
class CompanyStyleProvider @Inject constructor(private val mContext: Context) {

    private val mDrawableFavouriteBackgroundIcon = R.drawable.ic_favourite
    private val mDrawableFavouriteBackgroundIconActive = R.drawable.ic_favourite_active
    private val mDrawableFavouriteForegroundIcon = R.drawable.ic_star
    private val mDrawableFavouriteForegroundIconActive = R.drawable.ic_star_active

    private val mDrawableRippleLight = R.drawable.ripple_light
    private val mDrawableRippleDark = R.drawable.ripple_dark

    private val mColorProfitPlus = R.color.green
    private val mColorProfitMinus = R.color.red
    private val mColorHolderFirst = R.color.white
    private val mColorHolderSecond = R.color.whiteDark

    fun applyStyle(adaptiveCompany: AdaptiveCompany, index: Int) {
        adaptiveCompany.companyStyle.apply {
            holderBackground = getHolderBackground(index)
            rippleForeground = getRippleForeground(index)
            favouriteBackgroundIconResource = getBackgroundIconDrawable(adaptiveCompany.isFavourite)
            favouriteForegroundIconResource = getForegroundIconDrawable(adaptiveCompany.isFavourite)
            dayProfitBackground = getProfitBackground(adaptiveCompany.companyDayData.profit)
        }
    }

    fun getBackgroundIconDrawable(isFavourite: Boolean): Int {
        return if (isFavourite) mDrawableFavouriteBackgroundIconActive else mDrawableFavouriteBackgroundIcon
    }

    fun getForegroundIconDrawable(isFavourite: Boolean): Int {
        return if (isFavourite) mDrawableFavouriteForegroundIconActive else mDrawableFavouriteForegroundIcon
    }

    fun getProfitBackground(profit: String): Int {
        val prefix = profit.getOrNull(0)
        return if (prefix == '+') {
            getColor(mColorProfitPlus)
        } else getColor(mColorProfitMinus)
    }

    /**
     * Holder background depends of index at UI
     * */
    private fun getHolderBackground(index: Int): Int {
        return if (index % 2 == 0) {
            getColor(mColorHolderFirst)
        } else getColor(mColorHolderSecond)
    }

    /**
     * Ripple foreground depends of index at UI
     * */
    private fun getRippleForeground(index: Int): Int {
        return if (index % 2 == 0) mDrawableRippleDark else mDrawableRippleLight
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(mContext, color)
    }
}