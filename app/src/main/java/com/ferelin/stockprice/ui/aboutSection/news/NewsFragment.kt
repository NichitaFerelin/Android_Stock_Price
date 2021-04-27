package com.ferelin.stockprice.ui.aboutSection.news

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentNewsBinding
import com.ferelin.stockprice.utils.anim.AnimationManager
import com.ferelin.stockprice.viewModelFactories.CompanyViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsFragment(
    selectedCompany: AdaptiveCompany? = null
) : BaseFragment<NewsViewModel, NewsViewHelper>(), NewsClickListener {

    override val mViewHelper: NewsViewHelper = NewsViewHelper()
    override val mViewModel: NewsViewModel by viewModels {
        CompanyViewModelFactory(mCoroutineContext, mDataInteractor, selectedCompany)
    }

    private var mBinding: FragmentNewsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNewsBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)

        setUpRecyclerView()
        mBinding!!.fab.setOnClickListener {
            scrollToTop()
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch {
                mViewModel.notificationNewItems.collect {
                    withContext(mCoroutineContext.Main) {
                        showToast(resources.getString(R.string.notificationNewItems))
                        scrollToTop()
                    }
                }
            }
            launch {
                mViewModel.hasDataForRecycler.collect { hasData ->
                    withContext(mCoroutineContext.Main) {
                        switchWidgetsVisibility(hasData)
                    }
                }
            }
            launch {
                mViewModel.actionOpenUrl.collect {
                    try {
                        startActivity(it)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            requireContext(),
                            R.string.errorNoAppToOpenUrl,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            launch {
                mViewModel.actionShowError
                    .filter { it.isNotEmpty() }
                    .collect {
                        withContext(mCoroutineContext.Main) {
                            showToast(it)
                            mBinding!!.progressBar.visibility = View.INVISIBLE
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        /*
        * TODO Postpone
        * */
        mBinding!!.recyclerViewNews.adapter = null
        mBinding = null
    }

    override fun onNewsUrlClicked(position: Int) {
        mViewModel.onNewsUrlClicked(position)
    }

    private fun switchWidgetsVisibility(hasData: Boolean) {
        mBinding!!.apply {
            when {
                hasData && recyclerViewNews.visibility == View.GONE || progressBar.visibility == View.VISIBLE -> {
                    TransitionManager.beginDelayedTransition(root)
                    recyclerViewNews.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }
                !hasData && recyclerViewNews.visibility == View.VISIBLE || progressBar.visibility == View.INVISIBLE -> {
                    recyclerViewNews.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        mBinding!!.recyclerViewNews.apply {
            addItemDecoration(NewsItemDecoration(requireContext()))
            adapter = mViewModel.recyclerAdapter.also {
                it.setOnNewsClickListener(this@NewsFragment)
            }
        }
    }

    private fun scrollToTop() {
        val fabScaleOutCallback = object : AnimationManager() {
            override fun onAnimationEnd(animation: Animation?) {
                mBinding!!.fab.visibility = View.INVISIBLE
                mBinding!!.fab.scaleX = 1.0F
                mBinding!!.fab.scaleY = 1.0F
            }
        }
        if ((mBinding!!.recyclerViewNews.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 12) {
            val fadeInCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mBinding!!.recyclerViewNews.visibility = View.VISIBLE
                    mBinding!!.recyclerViewNews.smoothScrollToPosition(0)
                    mViewHelper.runScaleOut(mBinding!!.fab, fabScaleOutCallback)
                }
            }
            val fadeOutCallback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mBinding!!.recyclerViewNews.smoothScrollBy(
                        0,
                        -mBinding!!.recyclerViewNews.height
                    )
                }

                override fun onAnimationEnd(animation: Animation?) {
                    mBinding!!.recyclerViewNews.visibility = View.GONE
                    mBinding!!.recyclerViewNews.scrollToPosition(7)
                    mViewHelper.runFadeIn(mBinding!!.recyclerViewNews, fadeInCallback)
                }
            }
            mViewHelper.runFadeOut(mBinding!!.recyclerViewNews, fadeOutCallback)
        } else {
            mViewHelper.runScaleOut(mBinding!!.fab, fabScaleOutCallback)
            mBinding!!.recyclerViewNews.smoothScrollToPosition(0)
        }
    }
}