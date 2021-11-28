package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentTestBinding
import com.example.youngchemist.model.AnswerUi
import com.example.youngchemist.ui.screen.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding
    private lateinit var viewModel: TestFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    = FragmentTestBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageNumber = arguments?.getInt(PAGE_NUMBER_VALUE)
        viewModel= activity?.run {
            ViewModelProviders.of(this).get(TestFragmentViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        binding.tvQuestion.text = pageNumber?.let { viewModel.test?.tasks?.get(it)?.question }
        val testAnswersAdapter = TestAnswerAdapter(false,resources,viewModel.tasksUi[pageNumber!!].answersList)
        binding.rvAnswers.apply {
             layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
             adapter = testAnswersAdapter
        }

        viewModel.test?.let {
            testAnswersAdapter.answers = it.tasks[pageNumber!!].answers
        }

    }

    companion object {

        private const val PAGE_NUMBER_VALUE = "page_number_value"

        @JvmStatic
        fun newInstance(pageNumber: Int) =
            TestFragment().apply {
                arguments = Bundle().apply {
                    putInt(PAGE_NUMBER_VALUE, pageNumber)
                }
            }
    }
}