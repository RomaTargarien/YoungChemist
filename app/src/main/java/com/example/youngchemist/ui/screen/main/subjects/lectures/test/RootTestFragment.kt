package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentRootTestBinding
import com.example.youngchemist.databinding.FragmentTestBinding
import com.example.youngchemist.model.TaskUi
import com.example.youngchemist.model.TestUi
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.toPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class RootTestFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentRootTestBinding
    private lateinit var viewModel: TestFragmentViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= activity?.run {
            ViewModelProviders.of(this).get(TestFragmentViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        binding.viewModel = viewModel
        binding.bmSheet.viewModel = viewModel

        viewModel.testState.observe(viewLifecycleOwner, {
            val data = it.getContentIfNotHandled()
            when (data) {
                is ResourceNetwork.Success -> {
                    viewModel.goForward()
                    binding.bmSheet.ivForwardTest.isVisible = true
                }
                is ResourceNetwork.Error -> {

                }
                is ResourceNetwork.Loading -> {

                }
            }
        })
        binding.bmSheet.ivBackTest.setOnClickListener {
            viewModel.goBack()
        }
        binding.bmSheet.ivForwardTest.setOnClickListener {
            viewModel.goForward()
        }
        viewModel.currentPage.observe(viewLifecycleOwner,{ pageNumber ->
            when {
                pageNumber == 0 -> {
                    binding.bmSheet.ivBackTest.isVisible = false
                    replaceFragment(TestFragment.newInstance(pageNumber))
                }
                pageNumber == (viewModel.test?.tasks?.size?.minus(1)) -> {
                    binding.bmSheet.ivForwardTest.isVisible = false
                    replaceFragment(TestFragment.newInstance(pageNumber))
                }
                pageNumber < 0 -> {

                }
                else -> {
                    binding.bmSheet.ivBackTest.isVisible = true
                    binding.bmSheet.ivForwardTest.isVisible = true
                    replaceFragment(TestFragment.newInstance(pageNumber))
                }
            }
        })

        val bottomSheet = binding.bmSheet.bottomSheetContainer
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val offset =
                    ((binding.bmSheet.bottomSheetContainer.width / 2) - (binding.bmSheet.tvTestTitle.width) / 2) -resources.getDimension(R.dimen.test_margin_start)
                binding.bmSheet.tvTestTitle.animate().translationX((1 - slideOffset) * offset)
                    .setDuration(0).start()
                binding.bmSheet.appCompatButton2.animate().alpha(slideOffset).setDuration(0).start()
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.test_content, fragment).commit()
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
}