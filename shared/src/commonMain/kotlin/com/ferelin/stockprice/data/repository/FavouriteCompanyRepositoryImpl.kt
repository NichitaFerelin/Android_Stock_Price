package com.ferelin.stockprice.data.repository

import com.ferelin.stockprice.data.mapper.FavouriteCompanyMapper
import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.common.domain.repository.FavouriteCompanyRepository
import com.ferelin.stockprice.data.entity.favouriteCompany.FavouriteCompanyDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FavouriteCompanyRepositoryImpl(
  private val dao: FavouriteCompanyDao,
) : FavouriteCompanyRepository {

  override val favouriteCompanies: Flow<List<CompanyId>>
    get() = dao.getAll().map { it.map(FavouriteCompanyMapper::map) }

  override suspend fun addToFavourite(companyId: CompanyId) {
    dao.insert(companyId.value)
  }

  override suspend fun removeFromFavourite(companyId: CompanyId) {
    dao.eraseBy(companyId.value)
  }

  override suspend fun eraseAll(clearCloud: Boolean) {
    dao.eraseAll()
  }
}