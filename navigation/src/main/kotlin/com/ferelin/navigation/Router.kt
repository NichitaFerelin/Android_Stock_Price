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

interface Router {

    fun bind(activity: FragmentActivity)

    fun unbind()

    fun back()

    fun toStartFragment()

    fun fromLoadingToStocksPager(data: Any? = null)

    fun fromStocksPagerToSearch(data: Any? = null)

    fun fromDefaultStocksToAbout(data: Any? = null)

    fun fromFavouriteStocksToAbout(data: Any? = null)

    fun fromSearchToAbout(data: Any? = null)
}