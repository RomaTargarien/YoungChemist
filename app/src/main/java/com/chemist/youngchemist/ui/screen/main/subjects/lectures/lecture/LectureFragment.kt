package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.FragmentLectureBinding
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.model.ui.LectureUi
import com.chemist.youngchemist.ui.base.intent_3d.Intent3DCreator
import com.chemist.youngchemist.ui.listeners.OnPageNumberChangedListener
import com.chemist.youngchemist.ui.listeners.OnUriGetting
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture_base.StartTestDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("SetJavaScriptEnabled")
class LectureFragment : Fragment() {

    private lateinit var binding: FragmentLectureBinding
    private val viewModel: LectureFragmentViewModel by viewModels()
    private lateinit var lecture: LectureUi
    private val _isTheLastPageFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isTheLastPageFlow: StateFlow<Boolean> = _isTheLastPageFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            (it.getParcelable(LECTURE_PARAM) as LectureUi?)?.let { lectureParam ->
                lecture = lectureParam
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentLectureBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.bmSheet.viewModel = viewModel
        lecture.test?.let { viewModel.getTest(it) }
        initializeLecturePagesViewPager()
        initializeBottomSheetPagination()
        initializeBottomSheetRecyclerView()
        observeButtonBeginTestVisibility()
        binding.bnBeginTest.setOnClickListener {
            lecture.test?.let { showStartTestDialogFragment(it) }
        }
    }

    private fun initializeLecturePagesViewPager() {
        val adapter = PageAdapter()
        adapter.pages = lecture.data
        binding.vpLecturePages.adapter = adapter
        val size = lecture.data.size
        adapter.setOnEventListener(object : OnUriGetting {
            override fun onUriGetted(uri: String) {
                startActivity(Intent3DCreator.create3DIntent(uri))
            }
        })
        binding.vpLecturePages.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onPageChanged(position)
                binding.bmSheet.rvPagesPagination.smoothScrollToPosition(position)
                binding.tvPageNumber.text = resources.getString(
                    R.string.page_lecture_number_from_all_pages,
                    (position + 1).toString(),
                    size.toString()
                )
                lifecycleScope.launch {
                    _isTheLastPageFlow.emit((position + 1) == size)
                }
            }
        })
    }

    private fun initializeBottomSheetRecyclerView() {
        val pagesPaginationAdapter = PagesPaginationAdapter()
        binding.bmSheet.rvPagesPagination.setOnEventListener(object : OnPageNumberChangedListener {
            override fun onPageNumberChanged(page: Int) {
                binding.vpLecturePages.currentItem = page
            }
        })
        binding.bmSheet.rvPagesPagination.initialize(pagesPaginationAdapter)
        binding.bmSheet.rvPagesPagination.setViewsToChangeColor(listOf(R.id.list_item_page_number_background))
        pagesPaginationAdapter.setItems(lecture.data)
        pagesPaginationAdapter.setOnClickListener {
            binding.vpLecturePages.currentItem = it
        }
    }

    private fun initializeBottomSheetPagination() {
        val bottomSheet = binding.bmSheet.bottomSheetContainer
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        viewModel.isPaginationVisible.observe(viewLifecycleOwner, {
            bottomSheetBehavior.state =
                if (it) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        })
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.bmSheet.ivLeftArrowUp.animate().rotation(slideOffset * 180).setDuration(0)
                    .start()
                binding.bmSheet.ivRightArrowUp.animate().rotation(-slideOffset * 180).setDuration(0)
                    .start()
            }
        })
        binding.bmSheet.rvPagesPagination.setOnEventListener(object : OnPageNumberChangedListener {
            override fun onPageNumberChanged(page: Int) {
                binding.vpLecturePages.currentItem = page
            }
        })
    }

    private fun showStartTestDialogFragment(test: Test) {
        val startTestDialogFragment = StartTestDialogFragment(viewModel, test)
        startTestDialogFragment.show(requireActivity().supportFragmentManager, "dialog")
    }

    private fun observeButtonBeginTestVisibility() {
        lifecycleScope.launch {
            combine(
                viewModel.hasTheTestBeenDoneFlow,
                isTheLastPageFlow
            ) { hasTestBeenDone, isLastPage ->
                Pair(hasTestBeenDone, isLastPage)
            }.collect {
                val isVisible = !it.first && it.second
                TransitionManager.beginDelayedTransition(binding.beginTestContainer)
                binding.bnBeginTest.isVisible = isVisible
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveProgress(lecture.userProgress, lecture.data.size)
    }

    companion object {
        private const val LECTURE_PARAM = "lectures.lecture"

        fun newInstance(lecture: LectureUi) =
            LectureFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LECTURE_PARAM, lecture)
                }
            }
    }
}