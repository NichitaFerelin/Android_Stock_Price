package com.ferelin.remote.network.stockCandles

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StockCandlesApi {

    @GET("stock/candle")
    fun getStockCandle(
        @Query("symbol") symbol: String,
        @Query("token") token: String,
        @Query("from") from: Double,
        @Query("to") to: Double
    ): Call<StockCandlesResponse.Success>
}