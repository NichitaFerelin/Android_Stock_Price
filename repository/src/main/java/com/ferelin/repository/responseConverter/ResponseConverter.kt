package com.ferelin.repository.responseConverter

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

import com.ferelin.local.models.Company
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.Responses
import com.ferelin.local.responses.SearchesResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.api.stockCandles.StockCandlesResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.utils.Api
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.Time
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [ResponseConverter] is used to convert responses for UI from local/remote modules.
 */
@Singleton
class ResponseConverter @Inject constructor(private val mAdapter: DataAdapter) :
    ResponseConverterHelper {

    override fun convertCompaniesResponse(
        response: CompaniesResponse
    ): RepositoryResponse<List<AdaptiveCompany>> {
        return if (response is CompaniesResponse.Success) {
            val preparedData = when (response.code) {
                Responses.LOADED_FROM_JSON -> response.companies.map {
                    mAdapter.toAdaptiveCompanyFromJson(it)
                }
                else -> response.companies.map { mAdapter.toAdaptiveCompany(it) }
            }
            RepositoryResponse.Success(data = preparedData)
        } else RepositoryResponse.Failed()
    }

    override fun convertWebSocketResponse(response: BaseResponse<WebSocketResponse>): RepositoryResponse<AdaptiveWebSocketPrice> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as WebSocketResponse
            val formattedPrice = mAdapter.formatPrice(itemResponse.lastPrice)
            RepositoryResponse.Success(
                owner = itemResponse.symbol,
                data = AdaptiveWebSocketPrice(
                    formattedPrice,
                    mAdapter.buildProfitString(
                        currentPrice = itemResponse.lastPrice,
                        previousPrice = response.additionalMessage?.toDouble() ?: 0.0
                    )
                )
            )
        } else RepositoryResponse.Failed()
    }

    override fun convertStockCandlesResponse(
        response: BaseResponse<StockCandlesResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyHistory> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as StockCandlesResponse
            RepositoryResponse.Success(
                owner = symbol,
                data = AdaptiveCompanyHistory(
                    itemResponse.openPrices.map { mAdapter.formatPrice(it) },
                    itemResponse.highPrices.map { mAdapter.formatPrice(it) },
                    itemResponse.lowPrices.map { mAdapter.formatPrice(it) },
                    itemResponse.closePrices.map { mAdapter.formatPrice(it) },
                    itemResponse.timestamps.map {
                        mAdapter.fromLongToDateStr(Time.convertMillisFromResponse(it))
                    }
                )
            )
        } else {
            when (response.responseCode) {
                Api.RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.Limit)
                else -> RepositoryResponse.Failed()
            }
        }
    }

    override fun convertCompanyProfileResponse(
        response: BaseResponse<CompanyProfileResponse>,
        symbol: String,
    ): RepositoryResponse<AdaptiveCompanyProfile> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as CompanyProfileResponse
            RepositoryResponse.Success(
                owner = symbol,
                data = AdaptiveCompanyProfile(
                    mAdapter.adaptName(itemResponse.name),
                    symbol,
                    itemResponse.logoUrl,
                    itemResponse.country,
                    mAdapter.adaptPhone(itemResponse.phone),
                    itemResponse.webUrl,
                    itemResponse.industry,
                    itemResponse.currency,
                    mAdapter.formatPrice(itemResponse.capitalization)
                )
            )
        } else RepositoryResponse.Failed()
    }

    override fun convertStockSymbolsResponse(response: BaseResponse<StockSymbolResponse>): RepositoryResponse<AdaptiveStocksSymbols> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as StockSymbolResponse
            RepositoryResponse.Success(data = AdaptiveStocksSymbols(itemResponse.stockSymbols))
        } else RepositoryResponse.Failed()
    }

    override fun convertCompanyNewsResponse(
        response: BaseResponse<List<CompanyNewsResponse>>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as List<CompanyNewsResponse>

            val ids = mutableListOf<Double>()
            val headlines = mutableListOf<String>()
            val summaries = mutableListOf<String>()
            val sources = mutableListOf<String>()
            val dateTimes = mutableListOf<Double>()
            val previewImageUrls = mutableListOf<String>()
            val browserUrls = mutableListOf<String>()

            itemResponse.forEach {
                ids.add(it.newsId)
                headlines.add(it.headline)
                summaries.add(it.newsSummary)
                sources.add(it.newsSource)
                dateTimes.add(it.dateTime)
                previewImageUrls.add(it.previewImageUrl)
                browserUrls.add(it.newsBrowserUrl)
            }

            RepositoryResponse.Success(
                owner = symbol,
                data = AdaptiveCompanyNews(
                    ids.map {
                        it.toString().substringBefore(".")
                    }.toList(),
                    headlines.toList(),
                    summaries.toList(),
                    sources.toList(),
                    dateTimes.map {
                        mAdapter.fromLongToDateStr(mAdapter.convertMillisFromResponse(it.toLong()))
                    }.toList(),
                    browserUrls.toList(),
                    previewImageUrls.toList()
                )
            )
        } else {
            when (response.responseCode) {
                Api.RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.Limit)
                else -> RepositoryResponse.Failed()
            }
        }
    }

    override fun convertCompanyQuoteResponse(response: BaseResponse<CompanyQuoteResponse>): RepositoryResponse<AdaptiveCompanyDayData> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as CompanyQuoteResponse
            RepositoryResponse.Success(
                owner = response.additionalMessage,
                data = AdaptiveCompanyDayData(
                    mAdapter.formatPrice(itemResponse.currentPrice),
                    mAdapter.formatPrice(itemResponse.previousClosePrice),
                    mAdapter.formatPrice(itemResponse.openPrice),
                    mAdapter.formatPrice(itemResponse.highPrice),
                    mAdapter.formatPrice(itemResponse.lowPrice),
                    mAdapter.buildProfitString(itemResponse.currentPrice, itemResponse.openPrice)
                )
            )
        } else {
            when (response.responseCode) {
                Api.RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.Limit)
                else -> RepositoryResponse.Failed(owner = response.additionalMessage)
            }
        }
    }

    override fun convertSearchesForResponse(response: SearchesResponse): RepositoryResponse<List<AdaptiveSearchRequest>> {
        return if (response is SearchesResponse.Success) {
            val convertedData = response.data.map { AdaptiveSearchRequest(it) }
            RepositoryResponse.Success(data = convertedData)
        } else RepositoryResponse.Failed()
    }

    override fun convertCompaniesForInsert(companies: List<AdaptiveCompany>): List<Company> {
        return companies.map { convertCompanyForInsert(it) }
    }

    override fun convertCompanyForInsert(company: AdaptiveCompany): Company {
        return mAdapter.toDatabaseCompany(company)
    }

    override fun convertSearchesForInsert(search: List<AdaptiveSearchRequest>): Set<String> {
        val dataSet = mutableSetOf<String>()
        search.forEach { dataSet.add(it.searchText) }
        return dataSet
    }

    override fun convertFirstTimeLaunchStateToResponse(state: Boolean?): RepositoryResponse<Boolean> {
        return state?.let {
            RepositoryResponse.Success(data = state)
        } ?: RepositoryResponse.Failed()
    }

    override fun convertAuthenticationResponse(response: BaseResponse<Boolean>): RepositoryResponse<RepositoryMessages> {
        return when (response.responseCode) {
            Api.VERIFICATION_COMPLETED -> RepositoryResponse.Success(data = RepositoryMessages.Ok)
            Api.VERIFICATION_CODE_SENT -> RepositoryResponse.Success(data = RepositoryMessages.CodeSent)
            Api.VERIFICATION_TOO_MANY_REQUESTS -> RepositoryResponse.Failed(message = RepositoryMessages.Limit)
            else -> RepositoryResponse.Failed()
        }
    }

    override fun convertRealtimeDatabaseResponse(response: BaseResponse<String?>): RepositoryResponse<String> {
        return when (response.responseCode) {
            Api.RESPONSE_OK -> RepositoryResponse.Success(data = response.responseData!!)
            Api.RESPONSE_END -> RepositoryResponse.Failed(message = RepositoryMessages.End)
            Api.RESPONSE_NO_DATA -> RepositoryResponse.Failed(message = RepositoryMessages.Empty)
            else -> RepositoryResponse.Failed()
        }
    }
}