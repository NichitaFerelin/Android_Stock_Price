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

package com.ferelin.repository.converter

import com.ferelin.repository.converter.helpers.apiConverter.ApiConverter
import com.ferelin.repository.converter.helpers.authenticationConverter.AuthenticationConverter
import com.ferelin.repository.converter.helpers.chatsConverter.ChatsConverter
import com.ferelin.repository.converter.helpers.companiesConverter.CompaniesConverter
import com.ferelin.repository.converter.helpers.messagesConverter.MessagesConverter
import com.ferelin.repository.converter.helpers.realtimeConverter.RealtimeDatabaseConverter
import com.ferelin.repository.converter.helpers.searchRequestsConverter.SearchRequestsConverter
import com.ferelin.repository.converter.helpers.webSocketConverter.WebSocketConverter

/**
 * [ConverterMediator] is used to convert models between modules.
 */
interface ConverterMediator :
    ApiConverter,
    AuthenticationConverter,
    CompaniesConverter,
    MessagesConverter,
    RealtimeDatabaseConverter,
    SearchRequestsConverter,
    WebSocketConverter,
    ChatsConverter