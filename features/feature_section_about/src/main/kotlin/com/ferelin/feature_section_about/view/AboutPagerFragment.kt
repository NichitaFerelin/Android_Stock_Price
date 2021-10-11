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

package com.ferelin.feature_section_about.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.ferelin.core.params.AboutParams
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.feature_section_about.databinding.FragmentAboutPagerBinding
import com.ferelin.feature_section_about.viewModel.AboutPagerViewModel
import javax.inject.Inject

class AboutPagerFragment : BaseFragment<FragmentAboutPagerBinding>() {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<AboutPagerViewModel>

    private val mViewModel: AboutPagerViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAboutPagerBinding
        get() = FragmentAboutPagerBinding::inflate

    fun unpackArs(args: Bundle) {
        args[sAboutParamsKey]?.let { params ->
            if (params is AboutParams) {

            }
        }
    }

    companion object {

        private const val sAboutParamsKey = "about-params"

        fun newInstance(data: Any?): AboutPagerFragment {
            return AboutPagerFragment().also {
                if (data is AboutParams) {
                    it.arguments = bundleOf(sAboutParamsKey to data)
                }
            }
        }
    }
}