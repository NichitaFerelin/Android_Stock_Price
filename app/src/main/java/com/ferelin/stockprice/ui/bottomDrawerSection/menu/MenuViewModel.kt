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

package com.ferelin.stockprice.ui.bottomDrawerSection.menu

import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.adapter.MenuItem
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.adapter.MenuItemsAdapter
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : BaseViewModel() {

    private val mMenuAdapter = MenuItemsAdapter().apply {
        setHasStableIds(true)
    }
    val menuItemsAdapter: MenuItemsAdapter
        get() = mMenuAdapter

    val stateIsUserRegister: StateFlow<Boolean?>
        get() = mDataInteractor.stateUserRegister

    val isUserAuthenticated: Boolean
        get() = mDataInteractor.isUserLogged()

    val stateMenuItems: StateFlow<DataNotificator<List<MenuItem>>>
        get() = mDataInteractor.stateMenuItems

    val sharedLogOut: SharedFlow<Unit>
        get() = mDataInteractor.sharedLogOut

    var isWaitingForLoginResult = false
    var isWaitingForRegisterResult = false

    override fun initObserversBlock() {
        // Do nothing
    }

    fun onLogOut() {
        mAppScope.launch { mDataInteractor.logOut() }
    }
}