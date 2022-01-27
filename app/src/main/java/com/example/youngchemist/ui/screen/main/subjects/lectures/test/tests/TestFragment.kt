package com.example.youngchemist.ui.screen.main.subjects.lectures.test.tests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.youngchemist.databinding.FragmentTestBinding
import com.example.youngchemist.model.Task
import com.example.youngchemist.model.ui.AnswerUi
import com.example.youngchemist.ui.base.decorators.HorizontalItemDecoration
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.TestAnswerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding
    private val viewModel: TestFragmentViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTestBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageNumber = arguments?.getInt(PAGE_NUMBER_VALUE) as Int

        lifecycleScope.launch {
            viewModel.test.filterNotNull().combine(viewModel.tasksUi) { tasksUI, test ->
                Pair(tasksUI, test)
            }.collect { pair ->
                val task = pair.first.tasks[pageNumber]
                val answersList = pair.second[pageNumber].answersList
                showQuestion(task.question)
                showPicture(task.imageToQuestionUrl)
                showAnswers(task, answersList, pageNumber)
            }
        }
    }

    private fun showPicture(imageToQuestionUrl: String) {
        if (imageToQuestionUrl.isNotEmpty()) {
            binding.ivQuestion.isVisible = true
            binding.ivQuestion.load(imageToQuestionUrl)
        }
    }

    private fun showQuestion(question: String) {
        binding.tvQuestion.text = question
    }

    private fun showAnswers(task: Task, answersList: List<AnswerUi>, pageNumber: Int) {
        val itemDecoration = HorizontalItemDecoration(20)
        val testAnswerAdapter = TestAnswerAdapter(
            task.multipleAnswersAvailable,
            resources,
            answersList
        )
        testAnswerAdapter.answers = task.answers
        binding.rvAnswers.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(itemDecoration)
            adapter = testAnswerAdapter
        }
        testAnswerAdapter.setOnClickListener {
            viewModel.udpateTasksUi(pageNumber, it)
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