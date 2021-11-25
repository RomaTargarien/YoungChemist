package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentTestBinding
import com.example.youngchemist.ui.util.ResourceNetwork
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentTestBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ViewPagerTestAdapter(resources)
        binding.vpTest.adapter = adapter
        binding.vpTest.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    binding.ivBackTest.isVisible = false
                } else if (position + 1 == testSize) {
                    binding.ivForwardTest.isVisible = false
                } else {
                    binding.ivForwardTest.isVisible = true
                    binding.ivBackTest.isVisible = true
                }
                binding.progressBar.progress = (((position.toFloat()+1)/(testSize))*100).toInt()
                binding.tvTestPagination.text = "${position+1} из $testSize вопросов"
            }
        })
        binding.ivBackTest.setOnClickListener {
            binding.vpTest.currentItem = binding.vpTest.currentItem - 1
        }
        binding.ivForwardTest.setOnClickListener {
            binding.vpTest.currentItem = binding.vpTest.currentItem + 1
        }
        viewModel.timeLeft.observe(viewLifecycleOwner,{
            binding.tvTimer.setText(it)
        })
        viewModel.testState.observe(viewLifecycleOwner, {
            when (it) {
                is ResourceNetwork.Success -> {
                    it.data?.let {
                        adapter.tasks = it.tasks
                        testSize = it.tasks.size
                        binding.tvTestTitle.text = it.testTitle
                        binding.tvTestPagination.text = "1 из $testSize вопросов"
                        binding.ivBackTest.isVisible = true
                        binding.ivForwardTest.isVisible = true
                    }

                }
                is ResourceNetwork.Error -> {

                }
                is ResourceNetwork.Loading -> {

                }
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