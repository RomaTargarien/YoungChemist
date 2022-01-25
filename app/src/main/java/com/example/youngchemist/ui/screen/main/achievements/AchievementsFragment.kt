package com.example.youngchemist.ui.screen.main.achievements

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.example.youngchemist.databinding.FragmentAchievementsBinding
import com.example.youngchemist.service.AchievementService
import com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list.VerticalItemVerticalDecoration
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.HorizontalItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AchievementsFragment : Fragment() {

    private val viewModel: AchievementsFragmentViewModel by viewModels()
    private lateinit var mService: AchievementService
    private lateinit var achievementsUnDoneAdapter: AchievementsUnDoneAdapter
    private lateinit var achievementsDoneAdapter: AchievementsDoneAdapter
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AchievementService.LocalBinder
            mService = binder.getService()
            observeAchievements()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    private lateinit var binding: FragmentAchievementsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentAchievementsBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        achievementsUnDoneAdapter = AchievementsUnDoneAdapter()
        achievementsDoneAdapter = AchievementsDoneAdapter()
        bindToService()
        initializeRecyclers()
        achievementsDoneAdapter.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.doneAchievementsContainer)
            binding.tvDoneAchievementTitle.text = if (it.second) it.first else "Выберите достижение"
            binding.tvDoneAchievementTitle.alpha = if (it.second) 1f else 0.5f
        }
    }

    private fun initializeRecyclers() {
        binding.rvAchievementsAll.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = achievementsUnDoneAdapter
        }
        binding.rvDoneAchievements.apply {
            adapter = achievementsDoneAdapter
            addItemDecoration(VerticalItemVerticalDecoration(5,5))
        }
    }

    private fun bindToService() {
        Intent(requireContext(), AchievementService::class.java).also { intent ->
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun observeAchievements() {
        mService.unDoneAchievements.observe(viewLifecycleOwner,{
            Log.d("TAG",Thread.currentThread().toString())
            achievementsUnDoneAdapter.submitList(it)
        })
        mService.doneAchievements.observe(viewLifecycleOwner,{
            achievementsDoneAdapter.submitList(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unbindService(connection)
    }
}