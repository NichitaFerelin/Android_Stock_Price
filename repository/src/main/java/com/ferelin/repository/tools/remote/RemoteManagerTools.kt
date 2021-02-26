package com.ferelin.repository.tools.remote

import com.ferelin.remote.RemoteManagerHelper
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandle.StockCandleResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
import com.ferelin.remote.utilits.Api
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utilits.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoteManagerTools(
    private val mRemoteManagerHelper: RemoteManagerHelper,
    private val mResponsesConfigurator: ResponsesConfiguratorHelper
) : RemoteManagerToolsHelper {

    override fun openConnection(
        dataToSubscribe: Collection<String>,
        companiesCurrency: Map<String, String>
    ): Flow<Response<HashMap<String, Any>>> {
        return mRemoteManagerHelper.openConnection(dataToSubscribe).map { response ->
            if (response.code == Api.RESPONSE_OK) {
                mResponsesConfigurator.configure(
                    response as WebSocketResponse,
                    companiesCurrency[response.symbol] ?: "USD"
                )
            } else Response.Failed(response.code)
        }
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<Response<HashMap<String, Any>>> {
        return mRemoteManagerHelper.loadStockCandle(symbol, from, to, resolution).map { response ->
            if (response.code == Api.RESPONSE_OK) {
                mResponsesConfigurator.configure(symbol, response as StockCandleResponse)
            } else Response.Failed(response.code)
        }
    }

    override fun loadCompanyProfile(symbol: String): Flow<Response<AdaptiveCompany>> {
        return mRemoteManagerHelper.loadCompanyProfile(symbol).map { response ->
            if (response.code == Api.RESPONSE_OK) {
                mResponsesConfigurator.configure(symbol, response as CompanyProfileResponse)
            } else Response.Failed(response.code)
        }
    }

    override fun loadStockSymbols(): Flow<Response<List<String>>> {
        return mRemoteManagerHelper.loadStockSymbols().map { response ->
            if (response.code == Api.RESPONSE_OK) {
                val okResponse = response as StockSymbolResponse
                Response.Success(okResponse.stockSymbols)
            } else Response.Failed(response.code)
        }
    }
}