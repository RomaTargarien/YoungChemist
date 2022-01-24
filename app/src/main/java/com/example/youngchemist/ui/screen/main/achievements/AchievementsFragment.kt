package com.example.youngchemist.ui.screen.main.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youngchemist.databinding.FragmentAchievementsBinding
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
        val achievementsAdapter = AchievementsAdapter()
        binding.rvAchievementsAll.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = achievementsAdapter
        }
        viewModel.getAchievements()
        viewModel.userAchievements.observe(viewLifecycleOwner,{
            achievementsAdapter.submitList(it)
        })
    }
}