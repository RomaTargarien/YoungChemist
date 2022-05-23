package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.FragmentLecturesListBinding
import com.chemist.youngchemist.model.Subject
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.ui.screen.main.MainFragmentViewModel
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture_base.StartTestDialogFragment
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview


@FlowPreview
@AndroidEntryPoint
class LecturesListFragment : Fragment() {

    private lateinit var binding: FragmentLecturesListBinding
    private val viewModel: LecturesListViewModel by viewModels()
    private val mainViewModel: MainFragmentViewModel by activityViewModels()
    private var subject: Subject? = null
    private lateinit var lecturesListAdapter: LecturesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            subject = it.getParcelable(SUBJECT_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentLecturesListBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        subject?.let {
            viewModel.getLectures(it.collectionId)
            binding.tvSubjectTitle.text = it.title
            loadImage(it)
        }
        initializeRecyclerView()
        setOnClickListeners()
        observeUserProgressCounts()
        showUnViewedAchievementNumber()
        viewModel.lecturesUiState.observe(viewLifecycleOwner) {
            when (it) {
                is ResourceNetwork.Success -> {
                    binding.ivReload.isVisible = true
                    binding.progressFlask.isVisible = false
                    it.data?.let {
                        lecturesListAdapter.submitList(it)
                    }
                }
                is ResourceNetwork.Error -> {
                    binding.ivReload.isVisible = false
                    binding.progressFlask.isVisible = false
                }
                is ResourceNetwork.Loading -> {
                    binding.ivReload.isVisible = false
                    binding.progressFlask.isVisible = true
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
    }

    private fun showStartTestDialogFragment(test: Test) {
        val startTestDialogFragment = StartTestDialogFragment(viewModel,test)
        startTestDialogFragment.show(requireActivity().supportFragmentManager, "dialog")
    }

    private fun initializeRecyclerView() {
        lecturesListAdapter = LecturesListAdapter()
        binding.rvLectures.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = lecturesListAdapter
        }
    }

    private fun observeUserProgressCounts() {
        viewModel.passedTestsCount.observe(viewLifecycleOwner) {
            binding.pbDoneTests.apply {
                progressMax = it.first.toFloat()
                setProgressWithAnimation(it.second.toFloat(), 1800)
                binding.tvTestsDone.text = it.second.toString()
            }
        }
        viewModel.readenLecturesCount.observe(viewLifecycleOwner) {
            binding.pbReadLectures.apply {
                progressMax = it.first.toFloat()
                setProgressWithAnimation(it.second.toFloat(), 1800)
            }
            binding.tvReadLectures.text = it.second.toString()
        }
    }

    private fun setOnClickListeners() {
        lecturesListAdapter.setOnClickListener {
            viewModel.navigateToLectureScreen(it)
        }
        lecturesListAdapter.setOnBeginTestListener {
            showStartTestDialogFragment(it)
        }
    }

    private fun loadImage(item: Subject) {
        Picasso.get()
            .load(item.icon_url)
            .placeholder(R.drawable.ic_icon_happy_flask)
            .into(binding.ivSubject)
    }

    private fun showUnViewedAchievementNumber() {
        mainViewModel.doneAchievements.observe(viewLifecycleOwner) {
            it.count {
                !it.wasViewed
            }.also {
                when (it) {
                    0 -> binding.tvAchievementUnViewedCount.isVisible = false
                    else -> binding.tvAchievementUnViewedCount.apply {
                        isVisible = true
                        text = it.toString()
                    }
                }
            }
        }
    }

    companion object {
        private const val SUBJECT_PARAM = "subject"

        @JvmStatic
        fun newInstance(subject: Subject) =
            LecturesListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SUBJECT_PARAM, subject)
                }
            }
    }
}