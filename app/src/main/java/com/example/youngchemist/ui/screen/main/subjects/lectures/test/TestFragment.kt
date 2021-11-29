package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentTestBinding
import com.example.youngchemist.model.AnswerUi
import com.example.youngchemist.ui.screen.main.MainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding
    private val viewModel: TestFragmentViewModel by viewModels({requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    = FragmentTestBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageNumber = arguments?.getInt(PAGE_NUMBER_VALUE)
        if (viewModel.test?.tasks?.get(pageNumber!!)?.imageToQuestionUrl?.isNotEmpty()!!) {
            binding.ivQuestion.isVisible = true
            binding.ivQuestion.load(viewModel.test?.tasks!![pageNumber!!].imageToQuestionUrl)

        }
        val itemDecoration = HorizontalItemDecoration(20)
        binding.tvQuestion.text = pageNumber?.let { viewModel.test?.tasks?.get(it)?.question }
        val testAnswersAdapter = viewModel.test?.tasks?.get(pageNumber!!)
            ?.let { TestAnswerAdapter(it.multipleAnswersAvailable,resources,viewModel.tasksUi[pageNumber].answersList) }
        binding.rvAnswers.apply {
             layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
             addItemDecoration(itemDecoration)
             adapter = testAnswersAdapter
        }
        viewModel.test?.let {
            testAnswersAdapter?.answers = it.tasks[pageNumber!!].answers
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