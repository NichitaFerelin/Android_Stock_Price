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

package com.ferelin.data_local.reposirotires

import com.ferelin.data_local.database.CryptoDao
import com.ferelin.data_local.mappers.CryptoMapper
import com.ferelin.domain.entities.Crypto
import com.ferelin.domain.repositories.CryptoRepo
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CryptoRepoImpl @Inject constructor(
    private val cryptoDao: CryptoDao,
    private val cryptoMapper: CryptoMapper,
    private val dispatchersProvider: DispatchersProvider
) : CryptoRepo {

    override suspend fun insertAll(cryptos: List<Crypto>) =
        withContext(dispatchersProvider.IO) {
            Timber.d("insert all (size = ${cryptos.size})")

            cryptoDao.insertAll(
                cryptosDBO = cryptos.map(cryptoMapper::map)
            )
        }
}