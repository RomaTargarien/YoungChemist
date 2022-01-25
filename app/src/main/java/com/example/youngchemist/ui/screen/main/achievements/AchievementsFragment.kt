package com.example.youngchemist.ui.screen.main.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.example.youngchemist.databinding.FragmentAchievementsBinding
import com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list.VerticalItemVerticalDecoration
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.HorizontalItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AchievementsFragment : Fragment() {

    private val viewModel: AchievementsFragmentViewModel by viewModels()

    private lateinit var binding: FragmentAchievementsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentAchievementsBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        viewModel.getAchievements()
        val achievementsUnDoneAdapter = AchievementsUnDoneAdapter()
        val achievementsDoneAdapter = AchievementsDoneAdapter()
        binding.rvAchievementsAll.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = achievementsUnDoneAdapter
        }
        binding.rvDoneAchievements.apply {
            adapter = achievementsDoneAdapter
            addItemDecoration(VerticalItemVerticalDecoration(10,10))
        }
        viewModel.unDoneAchievements.observe(viewLifecycleOwner,{
            achievementsUnDoneAdapter.submitList(it)
        })
        viewModel.doneAchievements.observe(viewLifecycleOwner,{
            achievementsDoneAdapter.submitList(it)
        })
        achievementsDoneAdapter.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.doneAchievementsContainer)
            binding.tvDoneAchievementTitle.text = it
            binding.tvDoneAchievementTitle.isVisible = true
        }
    }
}