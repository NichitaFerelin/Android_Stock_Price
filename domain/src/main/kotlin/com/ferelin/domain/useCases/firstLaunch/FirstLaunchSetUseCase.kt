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

package com.ferelin.domain.useCases.firstLaunch

import com.ferelin.domain.repositories.FirstLaunchRepo
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * [FirstLaunchSetUseCase] allows to interact with first time application launch
 * */
class FirstLaunchSetUseCase @Inject constructor(
    private val firstLaunchRepo: FirstLaunchRepo,
    private val dispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val externalScope: CoroutineScope
) {
    /**
     * Allows to cache first time launch state
     * */
    suspend fun set(isFirstLaunch: Boolean) {
        externalScope.launch(dispatchersProvider.IO) {
            firstLaunchRepo.cache(isFirstLaunch)
        }
    }
}