package com.ferelin.remote.network.stockSymbol

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StockSymbolApi {

    @GET("stock/symbol?exchange=US&mic=XNGS&token=c0lqrff48v6ol6tu571g")
    fun getStockSymbolList(@Query("token") token: String): Call<List<StockSymbolResponse.Success>>
}