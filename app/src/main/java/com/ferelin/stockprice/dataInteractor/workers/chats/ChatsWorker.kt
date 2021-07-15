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

package com.ferelin.stockprice.dataInteractor.workers.chats

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class ChatsWorker @Inject constructor(
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope
) : ChatsWorkerStates {

    private var mChats = arrayListOf<AdaptiveChat>()

    private val mStateUserChats =
        MutableStateFlow<DataNotificator<List<AdaptiveChat>>>(DataNotificator.None())
    override val stateUserChats: StateFlow<DataNotificator<List<AdaptiveChat>>>
        get() = mStateUserChats

    private val mSharedUserChatsUpdates = MutableSharedFlow<DataNotificator<AdaptiveChat>>()
    override val sharedUserChatUpdates: SharedFlow<DataNotificator<AdaptiveChat>>
        get() = mSharedUserChatsUpdates

    private var mChatsJob: Job? = null

    init {
        prepareChats()
    }

    fun onLogIn() {
        prepareChats()
    }

    fun onLogOut() {
        mAppScope.launch {
            mChatsJob?.cancel()
            mChatsJob = null
            mChats.clear()
            mStateUserChats.value = DataNotificator.None()
        }
    }

    fun createNewChat(associatedUserNumber: String) {
        mAppScope.launch {
            if (!mRepository.isUserAuthenticated()) {
                return@launch
            }

            val itemAtContainer = mChats.find { it.associatedUserNumber == associatedUserNumber }
            if (itemAtContainer == null) {
                mRepository.getUserNumber()?.let { userNumber ->
                    mRepository.cacheChatToRealtimeDb(
                        sourceUserNumber = userNumber,
                        secondSideUserNumber = associatedUserNumber
                    )
                }
            }
        }
    }

    private fun prepareChats() {
        mAppScope.launch {
            if (!mRepository.isUserAuthenticated()) {
                return@launch
            }

            mChatsJob = launch {
                mStateUserChats.value = DataNotificator.Loading()
                loadItemsFromLocalCache()
                loadItemsFromRemoteCache()
            }
        }
    }

    private suspend fun loadItemsFromLocalCache() {
        mRepository.getAllChatsFromLocalDb().let { cachedChatsResponse ->
            if (cachedChatsResponse is RepositoryResponse.Success) {
                val cachedChats = cachedChatsResponse.data

                val newNotificatorState = if (cachedChats.isNotEmpty()) {
                    mChats.addAll(cachedChats)
                    DataNotificator.DataPrepared(mChats)
                } else DataNotificator.NoData()

                mStateUserChats.value = newNotificatorState
            }
        }
    }

    private suspend fun loadItemsFromRemoteCache() {
        mRepository.getUserNumber()?.let { userNumber ->
            mRepository.getUserChatsFromRealtimeDb(userNumber).collect { response ->
                if (response is RepositoryResponse.Success) {
                    onNewItem(response)
                }
            }
        }
    }

    private suspend fun onNewItem(response: RepositoryResponse.Success<AdaptiveChat>) {
        // TODO check realtime database .push() generated id
        // TODO messages worker also
        if (mChats.isNotEmpty()) {
            val responseItemAtContainerIndex = mChats.binarySearch(response.data.id - 1) { it.id }
            if (responseItemAtContainerIndex < 0) {
                val newIndex = abs(responseItemAtContainerIndex)
                mChats.add(newIndex, response.data)
                mSharedUserChatsUpdates.emit(DataNotificator.NewItemAdded(response.data))
                mAppScope.launch { mRepository.cacheChatToLocalDb(response.data) }
            }
        } else {
            mChats.add(response.data)
            mStateUserChats.value = DataNotificator.DataPrepared(listOf(response.data))
            mAppScope.launch { mRepository.cacheChatToLocalDb(response.data) }
        }
    }
}