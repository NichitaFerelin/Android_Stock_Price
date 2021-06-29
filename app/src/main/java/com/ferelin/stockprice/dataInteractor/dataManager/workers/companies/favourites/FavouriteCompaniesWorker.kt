package com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.favourites

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

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.dataInteractor.dataManager.stylesProvider.StylesProvider
import com.ferelin.stockprice.dataInteractor.dataManager.workers.errors.ErrorsWorker
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.parseDoubleFromStr
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [FavouriteCompaniesWorker] providing an ability to:
 *   - Observing [mStateFavouriteCompanies] to display a list of favourite companies.
 *   - Observing [mSharedFavouriteCompaniesUpdates] to add/remove item from list of favourite companies.
 *
 * Also [FavouriteCompaniesWorker] manually doing:
 *   - Subscribing favourite companies for live-time updates using [mRepositoryHelper].
 *   - Unsubscribing companies from live-time updates when it was removed from favourites using [mRepositoryHelper].
 *   - Control the limit of favourite companies @see [mCompaniesLimit] notifying with [mErrorsWorker].
 *   - Using [mLocalInteractor] to data caching.
 *   - Using [mStylesProvider] to change some stock fields that will be affect on stock's appearance.
 */

@Singleton
class FavouriteCompaniesWorker @Inject constructor(
    private val mStylesProvider: StylesProvider,
    private val mRepositoryHelper: Repository,
    private val mErrorsWorker: ErrorsWorker
) : FavouriteCompaniesWorkerStates {

    private var mFavouriteCompanies: ArrayList<AdaptiveCompany> = arrayListOf()
    override val favouriteCompanies: List<AdaptiveCompany>
        get() = mFavouriteCompanies

    private val mStateFavouriteCompanies =
        MutableStateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>(DataNotificator.Loading())
    override val stateFavouriteCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mStateFavouriteCompanies

    private val mSharedFavouriteCompaniesUpdates =
        MutableSharedFlow<DataNotificator<AdaptiveCompany>>()
    override val sharedFavouriteCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mSharedFavouriteCompaniesUpdates

    /*
    * To observe company by foreground service
    * */
    private val mStateCompanyForObserver = MutableStateFlow<AdaptiveCompany?>(null)
    override val stateCompanyForObserver: StateFlow<AdaptiveCompany?>
        get() = mStateCompanyForObserver

    /*
    * Subscribing over 50 items to live-time updates exceeds the limit of
    * web socket => over-limit-companies will not receive updates (or all companies depending
    * the api mood)
    * */
    private val mCompaniesLimit = 50

    fun onFavouriteCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
        mFavouriteCompanies = ArrayList(companies
            .filter { it.isFavourite }
            .sortedByDescending { it.favouriteOrderIndex }
        )
        subscribeCompaniesOnLiveTimeUpdates()
        mStateCompanyForObserver.value = mFavouriteCompanies.firstOrNull()
        mStateFavouriteCompanies.value = DataNotificator.DataPrepared(mFavouriteCompanies)
    }

    suspend fun onFavouriteCompanyChanged(company: AdaptiveCompany) {
        mSharedFavouriteCompaniesUpdates.emit(DataNotificator.ItemUpdatedCommon(company))
    }

    suspend fun addCompanyToFavourites(
        company: AdaptiveCompany,
        ignoreError: Boolean
    ): AdaptiveCompany? {
        if (isFavouritesCompaniesLimitExceeded()) {

            if (!ignoreError) {
                mErrorsWorker.onFavouriteCompaniesLimitReached()
            }

            return null
        }

        applyChangesToAddedFavouriteCompany(company)
        subscribeCompanyOnLiveTimeUpdates(company)
        mFavouriteCompanies.add(0, company)
        mStateCompanyForObserver.value = company
        mSharedFavouriteCompaniesUpdates.emit(DataNotificator.NewItemAdded(company))
        mRepositoryHelper.cacheCompanyToLocalDb(company)
        return company
    }

    suspend fun removeCompanyFromFavourites(company: AdaptiveCompany): AdaptiveCompany {
        applyChangesToRemovedFavouriteCompany(company)
        mRepositoryHelper.unsubscribeItemFromLiveTimeUpdates(company.companyProfile.symbol)
        mFavouriteCompanies.remove(company)

        if (mStateCompanyForObserver.value == company) {
            mStateCompanyForObserver.value = mFavouriteCompanies.firstOrNull()
        }

        mSharedFavouriteCompaniesUpdates.emit(DataNotificator.ItemRemoved(company))
        mRepositoryHelper.cacheCompanyToLocalDb(company)
        return company
    }

    fun subscribeCompaniesOnLiveTimeUpdates() {
        mFavouriteCompanies.forEach { subscribeCompanyOnLiveTimeUpdates(it) }
    }

    private fun isFavouritesCompaniesLimitExceeded(): Boolean {
        return mFavouriteCompanies.size >= mCompaniesLimit
    }

    private fun subscribeCompanyOnLiveTimeUpdates(company: AdaptiveCompany) {
        mRepositoryHelper.subscribeItemOnLiveTimeUpdates(
            company.companyProfile.symbol,
            parseDoubleFromStr(company.companyDayData.openPrice)
        )
    }

    private fun applyChangesToRemovedFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = false
            companyStyle.favouriteBackgroundIconResource =
                mStylesProvider.getBackgroundIconDrawable(isFavourite)
            companyStyle.favouriteForegroundIconResource =
                mStylesProvider.getForegroundIconDrawable(isFavourite)
        }
    }

    private fun applyChangesToAddedFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = true
            companyStyle.favouriteBackgroundIconResource =
                mStylesProvider.getBackgroundIconDrawable(isFavourite)
            companyStyle.favouriteForegroundIconResource =
                mStylesProvider.getForegroundIconDrawable(isFavourite)

            val orderIndex = mFavouriteCompanies.firstOrNull()?.favouriteOrderIndex?.plus(1) ?: 0
            favouriteOrderIndex = orderIndex
        }
    }
}