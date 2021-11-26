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
import androidx.viewpager2.widget.ViewPager2
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentTestBinding
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.toPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class TestFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentTestBinding
    private val viewModel: TestFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var testSize = 0
    private var previousPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentTestBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ViewPagerTestAdapter(resources)
        binding.vpTest.adapter = adapter
        binding.vpTest.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.bmSheet.ivBackTest.isVisible = false
                } else if (position + 1 == testSize) {
                    binding.bmSheet.ivForwardTest.isVisible = false
                } else {
                    binding.bmSheet.ivForwardTest.isVisible = true
                    binding.bmSheet.ivBackTest.isVisible = true
                }
                val from = (((previousPosition.toFloat() + 1) / (testSize)) * 100)
                val to = ((position.toFloat() + 1) / (testSize)) * 100
                previousPosition = position
                val pregressAnimator =
                    ObjectAnimator.ofInt(binding.progressBar, "progress", from.toInt(), to.toInt())
                pregressAnimator.duration = 1000
                pregressAnimator.start()
                binding.bmSheet.tvTestPagination.text = "${position + 1} из $testSize вопросов"
            }
        })

        binding.bmSheet.ivBackTest.setOnClickListener {
            binding.vpTest.currentItem = binding.vpTest.currentItem - 1
        }
        binding.bmSheet.ivForwardTest.setOnClickListener {
            binding.vpTest.currentItem = binding.vpTest.currentItem + 1
        }
        viewModel.timeLeft.observe(viewLifecycleOwner, {
            binding.tvTimer.setText(it)
        })
        adapter.setOnClickListener {
            for (item in it) {
                Log.d("TAG", "Номер теста - " + item.position.toString())
                if (item.answersList.isEmpty()) {
                    Log.d("TAG", "Не добрался")
                }
                for (answers in item.answersList) {
                    Log.d("TAG", answers.position.toString() + " " + answers.itIsRight.toString())
                }
            }
        }
        viewModel.testState.observe(viewLifecycleOwner, {
            when (it) {
                is ResourceNetwork.Success -> {
                    it.data?.let {
                        adapter.tasks = it.tasks
                        testSize = it.tasks.size
                        binding.bmSheet.tvTestTitle.text = it.testTitle
                        binding.bmSheet.tvTestPagination.text = "1 из $testSize вопросов"
                        binding.bmSheet.ivBackTest.isVisible = true
                        binding.bmSheet.ivForwardTest.isVisible = true
                    }

                }
                is ResourceNetwork.Error -> {

                }
                is ResourceNetwork.Loading -> {

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