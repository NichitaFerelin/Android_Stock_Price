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

package com.ferelin.local.di

import com.ferelin.domain.repositories.*
import com.ferelin.domain.repositories.companies.CompaniesLocalRepo
import com.ferelin.domain.repositories.searchRequests.SearchRequestsLocalRepo
import com.ferelin.domain.sources.CompaniesSource
import com.ferelin.local.reposirotires.*
import com.ferelin.local.sources.CompaniesSourceImpl
import dagger.Binds
import dagger.Module

@Module
interface DataLocalModuleBinds {

    @Binds
    fun provideCompaniesRepo(companiesRepoImpl: CompaniesRepoImpl): CompaniesLocalRepo

    @Binds
    fun provideFirstLaunchRepo(firstLaunchRepoImpl: FirstLaunchRepoImpl): FirstLaunchRepo

    @Binds
    fun provideNewsRepo(newsRepoImpl: NewsRepoImpl): NewsRepo

    @Binds
    fun providePastPriceRepo(pastPriceRepoImpl: PastPriceRepoImpl): PastPriceRepo

    @Binds
    fun provideProfileRepo(profileRepoImpl: ProfileRepoImpl): ProfileRepo

    @Binds
    fun provideSearchRequestsRepo(
        searchRequestsRepoImpl: SearchRequestsRepoImpl
    ): SearchRequestsLocalRepo

    @Binds
    fun provideStockPriceRepo(stockPriceRepoImpl: StockPriceRepoImpl): StockPriceRepo

    @Binds
    fun provideCompaniesSource(companiesSourceImpl: CompaniesSourceImpl): CompaniesSource
}