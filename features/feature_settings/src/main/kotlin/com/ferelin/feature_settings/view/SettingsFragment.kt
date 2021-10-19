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

package com.ferelin.feature_settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.adapter.options.itemDecoration.OptionDecoration
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_settings.R
import com.ferelin.feature_settings.databinding.FragmentSettingsBinding
import com.ferelin.feature_settings.viewModel.Event
import com.ferelin.feature_settings.viewModel.SettingsViewModel
import com.ferelin.shared.LoadState
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSettingsBinding
        get() = FragmentSettingsBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<SettingsViewModel>

    private val viewModel: SettingsViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            .apply { duration = 200L }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 200L }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 200L }
    }

    override fun initUi() {
        viewBinding.recyclerView.apply {
            adapter = viewModel.optionsAdapter
            addItemDecoration(OptionDecoration(requireContext()))
        }
    }

    override fun initUx() {
        viewBinding.imageBack.setOnClick(viewModel::onBackClick)
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch { observeMenuOptions() }
            launch { observeMessageEvent() }
        }
    }

    override fun onDestroyView() {
        viewBinding.recyclerView.adapter = null
        super.onDestroyView()
    }

    private suspend fun observeMenuOptions() {
        viewModel.optionsLoadState.collect { loadState ->
            if (loadState is LoadState.None) {
                viewModel.loadOptions()
            }
        }
    }

    private suspend fun observeMessageEvent() {
        viewModel.messageEvent.collect { event ->
            withContext(Dispatchers.Main) {
                when (event) {
                    Event.LOG_OUT_COMPLETE -> {
                        showTempSnackbar(getString(R.string.messageLogOutComplete))
                    }
                    Event.DATA_CLEARED_NO_NETWORK -> {
                        showTempSnackbar(getString(R.string.messageDataClearedNoNetwork))
                    }
                    Event.DATA_CLEARED -> {
                        showTempSnackbar(getString(R.string.messageDataCleared))
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(data: Any? = null): SettingsFragment {
            return SettingsFragment()
        }
    }
}