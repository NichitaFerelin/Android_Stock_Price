package com.ferelin.stockprice.viewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.shared.CoroutineContextProvider
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.ui.aboutSection.aboutPager.AboutPagerViewModel
import com.ferelin.stockprice.ui.aboutSection.chart.ChartViewModel
import com.ferelin.stockprice.ui.aboutSection.news.NewsViewModel
import kotlinx.coroutines.FlowPreview

@Suppress("UNCHECKED_CAST")
open class CompanyViewModelFactory(
    private val mCoroutineContext: CoroutineContextProvider,
    private val mDataInteractor: DataInteractor,
    private val mSelectedCompany: AdaptiveCompany?
) : ViewModelProvider.Factory {

    @FlowPreview
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ChartViewModel::class.java) -> {
                ChartViewModel(mCoroutineContext, mDataInteractor, mSelectedCompany) as T
            }
            modelClass.isAssignableFrom(AboutPagerViewModel::class.java) -> {
                AboutPagerViewModel(mCoroutineContext, mDataInteractor, mSelectedCompany) as T
            }
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(mCoroutineContext, mDataInteractor, mSelectedCompany) as T
            }
            else -> throw IllegalStateException("Unknown view model class: $modelClass")
        }
    }
}