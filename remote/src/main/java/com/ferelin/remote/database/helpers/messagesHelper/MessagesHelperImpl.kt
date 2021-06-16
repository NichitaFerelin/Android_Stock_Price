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

package com.ferelin.remote.database.helpers.messagesHelper

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.RealtimeValueEventListener
import com.ferelin.remote.utils.Api
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesHelperImpl @Inject constructor(
    private val mDatabaseFirebase: DatabaseReference
) : MessagesHelper {

    companion object {
        private const val sRelationsRef = "relations"
        private const val sMessagesRef = "messages"

        const val MESSAGE_SIDE_SOURCE = 'R'
        const val MESSAGE_SIDE_ASSOCIATED = 'L'
    }

    override fun addNewRelation(sourceUserLogin: String, secondSideUserLogin: String) {
        mDatabaseFirebase
            .child(sRelationsRef)
            .child(sourceUserLogin)
            .child(secondSideUserLogin)
            .setValue(secondSideUserLogin)
    }

    override fun getUserRelations(userLogin: String) = callbackFlow<BaseResponse<List<String>>> {
        mDatabaseFirebase
            .child(sRelationsRef)
            .child(userLogin)
            .addValueEventListener(object : RealtimeValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val relations = mutableListOf<String>()
                        for (relationSnapshot in snapshot.children) {
                            relationSnapshot.key?.let { associatedUserLogin ->
                                relations.add(associatedUserLogin)
                            }
                        }
                        trySend(
                            BaseResponse(
                                responseCode = Api.RESPONSE_OK,
                                additionalMessage = userLogin,
                                responseData = relations.toList()
                            )
                        )
                    } else trySend(BaseResponse(Api.RESPONSE_NO_DATA))
                }
            })
        awaitClose()
    }

    override fun getMessagesAssociatedWithSpecifiedUser(
        sourceUserLogin: String,
        secondSideUserLogin: String
    ) = callbackFlow<BaseResponse<List<Pair<Char, String>>>> {
        mDatabaseFirebase
            .child(sMessagesRef)
            .child(sourceUserLogin)
            .child(secondSideUserLogin)
            .addValueEventListener(object : RealtimeValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val messages = mutableListOf<Pair<Char, String>>()
                        for (messageSnapshot in snapshot.children) {
                            snapshot.key?.let { messageSideWithId ->
                                val messageSide = messageSideWithId[0]
                                messages.add(Pair(messageSide, snapshot.value.toString()))
                            }
                        }
                        trySend(
                            BaseResponse(
                                responseCode = Api.RESPONSE_OK,
                                additionalMessage = sourceUserLogin,
                                responseData = messages
                            )
                        )
                    } else trySend(BaseResponse(Api.RESPONSE_NO_DATA))
                }
            })
        awaitClose()
    }

    override fun addNewMessage(
        sourceUserLogin: String,
        secondSideUserLogin: String,
        messageId: String,
        message: String,
        sentFromSource: Boolean
    ) {
        val key = if (sentFromSource) MESSAGE_SIDE_SOURCE else MESSAGE_SIDE_ASSOCIATED
        val messageKey = key + messageId
        mDatabaseFirebase
            .child(sMessagesRef)
            .child(sourceUserLogin)
            .child(secondSideUserLogin)
            .child(messageKey)
            .setValue(message)
    }
}