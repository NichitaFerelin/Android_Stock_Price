package com.ferelin.remote.network.companyProfile

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

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CompanyProfileApi {
    @GET("stock/profile2")
    fun getCompanyProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): Call<CompanyProfileResponse>
}