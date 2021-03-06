package com.chemist.youngchemist.ui.screen.main.subjects.lectures.test.tests

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.FragmentRootTestBinding
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.ui.base.AnimationHelper
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.test.tests.dialogs.TestNoSaveDialogFragment
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.test.tests.dialogs.TestSaveDialogFragment
import com.chemist.youngchemist.ui.util.FragmentAnimationBehavior
import com.chemist.youngchemist.ui.util.animateProgress
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview


@FlowPreview
@AndroidEntryPoint
class RootTestFragment : Fragment() {

    private lateinit var test: Test
    private lateinit var binding: FragmentRootTestBinding
    private val viewModel: TestFragmentViewModel by viewModels()
    private lateinit var animationHelper: AnimationHelper
    private var previousPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            test = it.getParcelable<Test>(TEST) as Test
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentRootTestBinding.inflate(inflater, container, false).also { binding = it }.root

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.bmSheet.viewModel = viewModel
        viewModel.createUserTest(test)
        initializeAnimationHelper()
        initializeBottomSheet()
        observeTimeLeft()
        observeCurrentPageNumber()
        observeTimeIsUp()
        requireActivity().onBackPressedDispatcher.addCallback {
            showUnSavedDialogFragment()
        }
        binding.ivExit.setOnClickListener {
            showUnSavedDialogFragment()
        }
        binding.bmSheet.bnDone.setOnClickListener {
            showSavedDialogFragment()
        }
        binding.bmSheet.tvTestTitle.text = test.testTitle
    }

    private fun observeTimeIsUp() {
        viewModel.timeIsUp.observe(viewLifecycleOwner) {
            if (it) {
                binding.testContent.isVisible = false
                binding.bmSheet.bnDone.isEnabled = false
                binding.bmSheet.ivForwardTest.isEnabled = false
                binding.bmSheet.ivBackTest.isEnabled = false
                animationHelper.showMessage(resources.getString(R.string.time_is_up))
            } else {
                viewModel.saveTest(true, false)
            }
        }
    }

    private fun observeCurrentPageNumber() {
        viewModel.currentPage.observe(viewLifecycleOwner) {
            val pageNumber = it.first
            val animationBehavior = it.second
            if (pageNumber >= 0) {
                animateProgressBar(pageNumber)
                replaceFragment(TestFragment.newInstance(pageNumber), animationBehavior)
                toggleArrowsVisibility(pageNumber)
                val page = (pageNumber + 1).toString()
                val allPages = test.tasks.size.toString()
                binding.bmSheet.tvTestPagination.text =
                    resources.getString(R.string.page_number_from_all_pages, page, allPages)
                previousPosition = pageNumber
            }
        }
    }

    private fun observeTimeLeft() {
        viewModel.timeLeft.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }
    }

    private fun initializeBottomSheet() {
        val bottomSheet = binding.bmSheet.bottomSheetContainer
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {}

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val halfContainerWidth = binding.bmSheet.bottomSheetContainer.width / 2
            val halfTextWidth = (binding.bmSheet.tvTestTitle.width) / 2
            val offsetX =
                halfContainerWidth - halfTextWidth - resources.getDimension(R.dimen.test_margin_start)
            val offsetY = resources.getDimension(R.dimen.padding_small)
            binding.bmSheet.tvTestTitle.animate().translationX((1 - slideOffset) * offsetX)
                .setDuration(0).start()
            binding.bmSheet.tvTestTitle.animate().translationY((slideOffset - 1) * offsetY)
                .setDuration(0).start()
            binding.bmSheet.bnDone.animate().alpha(slideOffset).setDuration(0).start()
        }

    }

    private fun showUnSavedDialogFragment() {
        val testUnSaveDialogFragment = TestNoSaveDialogFragment(viewModel)
        testUnSaveDialogFragment.show(requireActivity().supportFragmentManager, "dialog")
    }

    private fun showSavedDialogFragment() {
        val testSaveDialogFragment = TestSaveDialogFragment(viewModel)
        testSaveDialogFragment.show(requireActivity().supportFragmentManager, "dialog")
    }

    private fun replaceFragment(fragment: Fragment, animationBehavior: FragmentAnimationBehavior) {
        val fragmentManager = childFragmentManager.beginTransaction()
        when (animationBehavior) {
            is FragmentAnimationBehavior.Enter -> {
                fragmentManager.setCustomAnimations(
                    R.anim.nav_default_enter_anim,
                    R.anim.nav_default_exit_anim,
                    R.anim.nav_default_pop_enter_anim,
                    R.anim.nav_default_pop_exit_anim
                )
            }
            is FragmentAnimationBehavior.Forward -> {
                fragmentManager.setCustomAnimations(
                    R.anim.enter_anim,
                    R.anim.exit_anim,
                    R.anim.pop_enter_anim,
                    R.anim.pop_exit_anim
                )
            }
            is FragmentAnimationBehavior.Back -> {
                fragmentManager.setCustomAnimations(
                    R.anim.pop_enter_anim,
                    R.anim.pop_exit_anim,
                    R.anim.enter_anim,
                    R.anim.exit_anim
                )
            }
        }
        fragmentManager.replace(R.id.test_content, fragment).commit()
    }

    private fun initializeAnimationHelper() {
        animationHelper = AnimationHelper(
            this.requireContext(),
            binding.cvTestEnd,
            binding.tvErrorText
        )
    }

    private fun animateProgressBar(pageNumber: Int) {
        val from = (((previousPosition.toFloat() + 1) / (test.tasks.size)) * 100)
        val to = ((pageNumber.toFloat() + 1) / (test.tasks.size)) * 100
        binding.progressBar.animateProgress(from.toInt(), to.toInt())
    }

    private fun toggleArrowsVisibility(pageNumber: Int) {
        binding.bmSheet.ivBackTest.isVisible = true
        binding.bmSheet.ivForwardTest.isVisible = true
        if (pageNumber == 0) {
            binding.bmSheet.ivBackTest.isVisible = false
        }
        if (pageNumber == test.tasks.size - 1) {
            binding.bmSheet.ivForwardTest.isVisible = false
        }
    }

    companion object {
        private const val TEST = "test"

        @JvmStatic
        fun newInstance(test: Test) =
            RootTestFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TEST,test)
                }
            }
    }
}