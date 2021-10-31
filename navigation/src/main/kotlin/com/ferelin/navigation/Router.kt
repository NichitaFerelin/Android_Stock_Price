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

package com.ferelin.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

interface Router {

    fun bind(activity: FragmentActivity)

    fun unbind()

    fun back()

    fun openUrl(url: String): Boolean

    fun openContacts(phone: String): Boolean

    fun shareText(text: String)

    fun toStartFragment()

    fun fromLoadingToStocksPager(
        params: Any? = null,
        onTransaction: ((FragmentTransaction) -> Unit)? = null
    )

    fun fromStocksPagerToSearch(
        params: Any? = null,
        onTransaction: ((FragmentTransaction) -> Unit)? = null
    )

    fun fromStocksPagerToSettings(
        params: Any? = null,
        onTransaction: ((FragmentTransaction) -> Unit)? = null
    )

    fun fromDefaultStocksToAbout(
        params: Any? = null,
        onTransaction: ((FragmentTransaction) -> Unit)? = null
    )

    fun fromFavouriteStocksToAbout(
        params: Any? = null,
        onTransaction: ((FragmentTransaction) -> Unit)? = null
    )

    fun fromSearchToAbout(
        params: Any? = null,
        onTransaction: ((FragmentTransaction) -> Unit)? = null
    )

    fun fromSettingsToLogin(
        params: Any? = null,
        onTransaction: ((FragmentTransaction) -> Unit)? = null
    )
}