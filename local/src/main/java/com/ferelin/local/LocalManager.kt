package com.ferelin.local

import com.ferelin.local.database.CompaniesManagerHelper
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.models.Company
import com.ferelin.local.prefs.StorePreferencesHelper
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.Responses
import com.ferelin.local.responses.SearchesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalManager(
    private val mJsonManagerHelper: JsonManagerHelper,
    private val mCompaniesManagerHelper: CompaniesManagerHelper,
    private val mStorePreferencesHelper: StorePreferencesHelper
) : LocalManagerHelper {

    override fun insertCompany(company: Company) {
        mCompaniesManagerHelper.insertCompany(company)
    }

    override fun insertAllCompanies(list: List<Company>) {
        mCompaniesManagerHelper.insertAllCompanies(list)
    }

    override fun updateCompany(company: Company) {
        mCompaniesManagerHelper.updateCompany(company)
    }

    override fun getAllCompanies(): Flow<List<Company>> {
        return mCompaniesManagerHelper.getAllCompanies()
    }

    override fun getAllCompaniesAsResponse(): Flow<CompaniesResponse> {
        return mCompaniesManagerHelper.getAllCompanies().map { databaseCompanies ->
            if (databaseCompanies.isEmpty()) {
                val localJsonCompanies = getCompaniesFromJson().first().also {
                    it.forEachIndexed { index, company ->
                        company.id = index
                    }
                }
                if (localJsonCompanies.isNotEmpty()) {
                    insertAllCompanies(localJsonCompanies)
                    CompaniesResponse.Success(
                        code = Responses.LOADED_FROM_JSON,
                        companies = localJsonCompanies
                    )
                } else CompaniesResponse.Failed
            } else CompaniesResponse.Success(companies = databaseCompanies)
        }
    }

    override fun getSearchesHistoryAsResponse(): Flow<SearchesResponse> {
        return getSearchesHistory().map {
            if (it != null) {
                SearchesResponse.Success(it)
            } else SearchesResponse.Failed
        }
    }

    override fun deleteCompany(company: Company) {
        mCompaniesManagerHelper.deleteCompany(company)
    }

    override fun getCompaniesFromJson(): Flow<List<Company>> {
        return mJsonManagerHelper.getCompaniesFromJson()
    }

    override fun getSearchesHistory(): Flow<Set<String>?> {
        return mStorePreferencesHelper.getSearchesHistory()
    }

    override suspend fun setSearchesHistory(requests: Set<String>) {
        mStorePreferencesHelper.setSearchesHistory(requests)
    }
}