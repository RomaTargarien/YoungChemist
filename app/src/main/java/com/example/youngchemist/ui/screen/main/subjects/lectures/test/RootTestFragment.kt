package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentRootTestBinding
import com.example.youngchemist.ui.base.AnimationHelper
import com.example.youngchemist.ui.util.FragmentAnimationBehavior
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.TestExitBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class RootTestFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentRootTestBinding
    private val viewModel: TestFragmentViewModel by viewModels()
    private lateinit var animationHelper: AnimationHelper
    private var previousPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        animationHelper = initializeAnimationHepler()
        viewModel.testState.observe(viewLifecycleOwner, {
            val data = it.getContentIfNotHandled()
            when (data) {
                is ResourceNetwork.Success -> {
                    viewModel.enterTest()
                    binding.progressFlask.isVisible = false
                }
                is ResourceNetwork.Error -> {
                    binding.progressFlask.isVisible = false
                }
                is ResourceNetwork.Loading -> {
                    binding.progressFlask.isVisible = true
                }
            }
        })

        viewModel.exitBehavior.observe(viewLifecycleOwner,{
            when (it) {
                is TestExitBehavior.ExitNoSave -> {
                    showUnSavedDialogFragment()
                }
                is TestExitBehavior.Exit -> {
                    viewModel.exit()
                }
            }
        })

        viewModel.timeLeft.observe(viewLifecycleOwner,{
            binding.tvTimer.text = it
        })

        viewModel.currentPage.observe(viewLifecycleOwner, {
            val pageNumber = it.first
            val animationBehavior = it.second
            if (pageNumber >= 0) {
                binding.bmSheet.bnDone.isVisible = true
                val from = (((previousPosition.toFloat() + 1) / (viewModel.tasksUi.size)) * 100)
                val to = ((pageNumber.toFloat() + 1) / (viewModel.tasksUi.size)) * 100
                val pregressAnimator =
                    ObjectAnimator.ofInt(binding.progressBar, "progress", from.toInt(), to.toInt())
                pregressAnimator.duration = 1000
                pregressAnimator.start()
                replaceFragment(TestFragment.newInstance(pageNumber),animationBehavior)
                binding.bmSheet.ivBackTest.isVisible = true
                binding.bmSheet.ivForwardTest.isVisible = true
                if (pageNumber == 0) {
                    binding.bmSheet.ivBackTest.isVisible = false
                }
                if (pageNumber == viewModel.tasksUi.size - 1) {
                    binding.bmSheet.ivForwardTest.isVisible = false
                }
                binding.bmSheet.tvTestPagination.text =
                    "${pageNumber + 1} вопрос из ${viewModel.tasksUi.size}"
                binding.bmSheet.tvTestTitle.text = viewModel.test?.testTitle

                previousPosition = pageNumber
            }
        })
        binding.ivExit.setOnClickListener {
            viewModel.tryExitTheTest()
        }
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.tryExitTheTest()
        }
        binding.bmSheet.bnDone.setOnClickListener {
            val testSaveDialogFragment = TestSaveDialogFragment(viewModel)
            testSaveDialogFragment.show(requireActivity().supportFragmentManager,"dialog")
        }
        val bottomSheet = binding.bmSheet.bottomSheetContainer
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        viewModel.timeIsUp.observe(viewLifecycleOwner,{
            if (it) {
                binding.testContent.isVisible = false
                binding.bmSheet.bnDone.isEnabled = false
                binding.bmSheet.ivForwardTest.isEnabled = false
                binding.bmSheet.ivBackTest.isEnabled = false
                animationHelper.showMessage("Время истекло!")
            } else {
                viewModel.saveTest()
            }

        })


        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val offset =
                    ((binding.bmSheet.bottomSheetContainer.width / 2) - (binding.bmSheet.tvTestTitle.width) / 2) - resources.getDimension(
                        R.dimen.test_margin_start
                    )
                val offset2 = resources.getDimension(R.dimen.padding_small)

                binding.bmSheet.tvTestTitle.animate().translationX((1 - slideOffset) * offset)
                    .setDuration(0).start()
                binding.bmSheet.tvTestTitle.animate().translationY((slideOffset -1) * offset2)
                    .setDuration(0).start()
                binding.bmSheet.bnDone.animate().alpha(slideOffset).setDuration(0).start()
            }
        })
    }

    private fun showUnSavedDialogFragment() {
        val testUnSaveDialogFragment = TestNoSaveDialogFragment(viewModel)
        testUnSaveDialogFragment.show(requireActivity().supportFragmentManager,"dialog")
    }

    private fun replaceFragment(fragment: Fragment,animationBehavior: FragmentAnimationBehavior) {
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initializeAnimationHepler() =
        AnimationHelper(
            this.requireContext(),
            binding.testContent,
            binding.cvTestEnd,
            binding.tvErrorText,
            binding.bmSheet.bnDone
        )
}