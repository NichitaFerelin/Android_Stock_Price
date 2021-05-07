package com.ferelin.stockprice.utils

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

/*
* Default class to set data to State/Shared Flow
* */
sealed class DataNotificator<out T>(val data: T? = null) {

    class DataPrepared<out T>(data: T) : DataNotificator<T>(data)

    class DataUpdated<out T>(data: T) : DataNotificator<T>(data)

    class NewItemAdded<out T>(data: T) : DataNotificator<T>(data)

    class ItemRemoved<out T>(data: T) : DataNotificator<T>(data)

    class ItemUpdatedCommon<out T>(data: T) : DataNotificator<T>(data)

    class ItemUpdatedQuote<out T>(data: T) : DataNotificator<T>(data)

    class ItemUpdatedLiveTime<out T>(data: T) : DataNotificator<T>(data)

    class Loading<out T> : DataNotificator<T>()
}