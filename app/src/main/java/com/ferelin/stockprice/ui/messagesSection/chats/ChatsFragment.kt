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

package com.ferelin.stockprice.ui.messagesSection.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentChatsBinding
import com.ferelin.stockprice.ui.messagesSection.addUser.DialogAddUser
import com.ferelin.stockprice.ui.messagesSection.chats.adapter.ChatClickListener
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.launch

class ChatsFragment :
    BaseFragment<FragmentChatsBinding, ChatsViewModel, ChatsViewController>(),
    ChatClickListener {

    override val mViewController = ChatsViewController()
    override val mViewModel: ChatsViewModel by viewModels()

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChatsBinding
        get() = FragmentChatsBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = 200L
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()
        mViewController.setArgumentsViewDependsOn(mViewModel.relationsAdapter)
        mViewModel.relationsAdapter.setOnClickListener(this)

        setFragmentResultListener(DialogAddUser.ADD_USER_REQUEST_KEY) { _, bundle ->
            mViewModel.onAddUserResult(bundle)
        }
    }

    override fun onRelationClicked(position: Int) {
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            mViewController.onRelationClicked(this@ChatsFragment, position)
        }
    }

    private fun setUpClickListeners() {
        mViewController.viewBinding.imageViewAdd.setOnClickListener {
            mViewController.onAddPersonClicked(this@ChatsFragment)
        }
    }
}