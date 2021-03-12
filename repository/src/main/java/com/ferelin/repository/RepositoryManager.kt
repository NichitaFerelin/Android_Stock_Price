package com.ferelin.repository

import android.content.Context
import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.database.CompaniesDatabase
import com.ferelin.local.database.CompaniesManager
import com.ferelin.local.json.JsonManager
import com.ferelin.local.prefs.StorePreferences
import com.ferelin.remote.RemoteManager
import com.ferelin.remote.RemoteManagerHelper
import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.dataConverter.DataConverter
import com.ferelin.repository.dataConverter.DataConverterHelper
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.SingletonHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepositoryManager private constructor(
    private val mRemoteManagerHelper: RemoteManagerHelper,
    private val mLocalManagerHelper: LocalManagerHelper,
    private val mDataConverterHelper: DataConverterHelper
) : RepositoryManagerHelper {

    override fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>> {
        return mLocalManagerHelper.getAllCompaniesAsResponse().map {
            mDataConverterHelper.convertCompaniesResponse(it)
        }
    }

    override fun openConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>> {
        return mRemoteManagerHelper.openConnection().map {
            mDataConverterHelper.convertWebSocketResponse(it)
        }
    }

    override fun subscribeItemToLiveTimeUpdates(symbol: String, openPrice: Double) {
        mRemoteManagerHelper.subscribeItem(symbol, openPrice)
    }

    override fun unsubscribeItemFromLiveTimeUpdates(symbol: String) {
        mRemoteManagerHelper.unsubscribeItem(symbol)
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<RepositoryResponse<AdaptiveCompanyHistory>> {
        return mRemoteManagerHelper.loadStockCandles(symbol, from, to, resolution).map {
            mDataConverterHelper.convertStockCandlesResponse(it, symbol)
        }
    }

    override fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>> {
        return mRemoteManagerHelper.loadCompanyProfile(symbol).map {
            mDataConverterHelper.convertCompanyProfileResponse(it, symbol)
        }
    }

    override fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStocksSymbols>> {
        return mRemoteManagerHelper.loadStockSymbols().map {
            mDataConverterHelper.convertStockSymbolsResponse(it)
        }
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<RepositoryResponse<AdaptiveCompanyNews>> {
        return mRemoteManagerHelper.loadCompanyNews(symbol, from, to).map {
            mDataConverterHelper.convertCompanyNewsResponse(it, symbol)
        }
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>> {
        return mRemoteManagerHelper.loadCompanyQuote(symbol, position).map {
            mDataConverterHelper.convertCompanyQuoteResponse(it)
        }
    }

    override fun saveCompanyData(adaptiveCompany: AdaptiveCompany) {
        val preparedForInsert = mDataConverterHelper.convertCompanyForInsert(adaptiveCompany)
        mLocalManagerHelper.updateCompany(preparedForInsert)
    }

    override fun getSearchesHistory(): Flow<RepositoryResponse<List<AdaptiveSearchRequest>>> {
        return mLocalManagerHelper.getSearchesHistoryAsResponse().map {
            mDataConverterHelper.convertSearchesForResponse(it)
        }
    }

    override suspend fun setSearchesHistory(requests: List<AdaptiveSearchRequest>) {
        val preparedForInsert = mDataConverterHelper.convertSearchesForInsert(requests)
        mLocalManagerHelper.setSearchesHistory(preparedForInsert)
    }

    companion object : SingletonHolder<RepositoryManager, Context>({
        val remoteHelper = RemoteManager(NetworkManager(), WebSocketConnector())
        val dataBase = CompaniesDatabase.getInstance(it)
        val preferences = StorePreferences(it)
        val localHelper = LocalManager(JsonManager(it), CompaniesManager(dataBase), preferences)
        val dataConverter = DataConverter()
        RepositoryManager(remoteHelper, localHelper, dataConverter)
    })
}