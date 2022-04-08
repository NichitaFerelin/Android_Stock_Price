package com.ferelin.stockprice.shared.ui.viewModel

import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.domain.usecase.CompanyUseCase
import com.ferelin.stockprice.shared.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.stockprice.shared.ui.DispatchersProvider
import com.ferelin.stockprice.shared.ui.viewData.StockViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

data class FavouriteStocksStateUi internal constructor(
  val companies: List<StockViewData> = emptyList(),
  val companiesLce: LceState = LceState.None
)

class FavouriteStocksViewModel internal constructor(
  companyUseCase: CompanyUseCase,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
  viewModelScope: CoroutineScope,
  dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
  favouriteCompanyUseCase,
  viewModelScope,
  dispatchersProvider,
  companyUseCase
) {
  private val viewModelState = MutableStateFlow(FavouriteStocksStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    companies
      .filterFavouritesOnly()
      .onEach(this::onCompanies)
      .launchIn(viewModelScope)

    companyUseCase.companiesLce
      .onEach(this::onCompaniesLce)
      .launchIn(viewModelScope)
  }

  private fun onCompanies(companies: List<StockViewData>) {
    viewModelState.update { it.copy(companies = companies) }
  }

  private fun onCompaniesLce(lceState: LceState) {
    viewModelState.update { it.copy(companiesLce = lceState) }
  }
}

internal fun Flow<List<StockViewData>>.filterFavouritesOnly(): Flow<List<StockViewData>> {
  return this.map { companies ->
    companies.filter { stock -> stock.isFavourite }
  }
}