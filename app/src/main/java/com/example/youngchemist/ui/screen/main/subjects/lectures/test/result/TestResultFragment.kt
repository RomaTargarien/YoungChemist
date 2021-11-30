package com.example.youngchemist.ui.screen.main.subjects.lectures.test.result

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentTestResultBinding
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.TestNoSaveDialogFragment
import com.example.youngchemist.ui.util.slideUpViews
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TestResultFragment : Fragment() {

    private lateinit var binding: FragmentTestResultBinding
    private val viewModel: TestResultFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentTestResultBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        slideUpViews(binding.tvTestResult,binding.bnBackToLectures,binding.bnBackToMainScreen)
        val mark = arguments?.getDouble(USER_MARK) as Double
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }

        when {
            (mark < 2) -> {
                binding.tvTestResult.text = resources.getString(R.string.very_bad)
            }
            (mark in 2.0..5.0) -> {
                binding.tvTestResult.text = resources.getString(R.string.bad)
            }
            (mark in 5.0..7.0) -> {
                binding.tvTestResult.text = resources.getString(R.string.not_so_bad)
            }
            (mark in 7.0..9.0) -> {
                binding.tvTestResult.text = resources.getString(R.string.good)
            }
            (mark in 9.0..10.0) -> {
                binding.tvTestResult.text = resources.getString(R.string.very_good)
            }
        }
        binding.tvMark.text = mark.toString()
        binding.pbMark.apply {
            progressMax = 10.0f
            setProgressWithAnimation(mark.toFloat(),1800)
            progressBarColor = Color.GREEN
        }
    }

    companion object {
       private const val USER_MARK = "user_mark"

        @JvmStatic
        fun newInstance(mark: Double) =
            TestResultFragment().apply {
                arguments = Bundle().apply {
                    putDouble(USER_MARK, mark)
                }
            }
    }
}