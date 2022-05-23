package com.chemist.youngchemist.ui.screen.main.achievements

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.chemist.youngchemist.databinding.FragmentAchievementsBinding
import com.chemist.youngchemist.ui.screen.main.MainFragmentViewModel
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.lectures_list.VerticalItemVerticalDecoration
import com.chemist.youngchemist.ui.util.ResourceNetwork
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@FlowPreview
@AndroidEntryPoint
class AchievementsFragment : Fragment() {

    private val viewModel: AchievementsFragmentViewModel by viewModels()
    private val mainViewModel: MainFragmentViewModel by activityViewModels()
    private lateinit var achievementsUnDoneAdapter: AchievementsUnDoneAdapter
    private lateinit var achievementsDoneAdapter: AchievementsDoneAdapter
    private val achivementNumberFlow: MutableStateFlow<Int?> = MutableStateFlow(null)

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
        initializeRecyclers()
        observeAchievements()
        achievementsDoneAdapter.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.doneAchievementsContainer)
            binding.tvDoneAchievementTitle.text = if (it.second) it.first else "Выберите достижение"
            binding.tvDoneAchievementTitle.alpha = if (it.second) 1f else 0.5f
        }
        viewModel.doneTestsCount.observe(viewLifecycleOwner) {
            animateNumber(it, binding.tvPassedTestsCount)
        }
        viewModel.savedModelsCount.observe(viewLifecycleOwner) {
            animateNumber(it, binding.tvSavedModelsCount)
        }
        viewModel.readenLecturesCount.observe(viewLifecycleOwner) {
            animateNumber(it, binding.tvReadenLecturesCount)
        }
    }

    private fun animateNumber(count: Int, textView: TextView) {
        val animator = ValueAnimator.ofInt(0, count)
        animator.duration = 500
        animator.addUpdateListener { animation -> textView.setText(animation.animatedValue.toString()) }
        animator.start()
    }

    private fun initializeRecyclers() {
        binding.rvAchievementsAll.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = achievementsUnDoneAdapter
        }
        binding.rvDoneAchievements.apply {
            adapter = achievementsDoneAdapter
            addItemDecoration(VerticalItemVerticalDecoration(5, 5))
        }
    }

    private fun observeAchievements() {
        mainViewModel.unDoneAchievements.observe(viewLifecycleOwner) {
            achievementsUnDoneAdapter.submitList(it)
        }
        mainViewModel.doneAchievements.observe(viewLifecycleOwner) {
            achievementsDoneAdapter.submitList(it)
            lifecycleScope.launch {
                achivementNumberFlow.emit(it.count { !it.wasViewed })
            }
            it.forEach {
                if (!it.wasViewed) {
                    viewModel.achievementWasViewed(it)
                }
            }
        }

        mainViewModel.doneAchievementsPercentage.observe(viewLifecycleOwner) {
            when (it) {
                is ResourceNetwork.Loading -> {

                }
                is ResourceNetwork.Success -> {
                    it.data?.let {
                        binding.tvDoneAchievementsPercentage.text = "$it%"
                        binding.pbDoneAchievementsPercentage.apply {
                            progressMax = 100f
                            setProgressWithAnimation(it.toFloat(), 1800)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val number = achivementNumberFlow.filterNotNull().first()
            if (number != 0) {
                animateFreshlyDoneAchievementsNumber(number)
            }
        }
    }

    private fun animateFreshlyDoneAchievementsNumber(number: Int) {
        lifecycleScope.launch(Dispatchers.Default) {
            launch(Dispatchers.Main) {
                android.transition.TransitionManager.beginDelayedTransition(binding.mainContainer)
                binding.tvDoneAchievementNumber.text = "+$number"
                binding.tvDoneAchievementNumber.isVisible = true
                val anim: Animation = AlphaAnimation(0.2f, 1.0f)
                anim.duration = 400
                anim.startOffset = 20
                anim.repeatMode = Animation.REVERSE
                anim.repeatCount = Animation.INFINITE
                binding.tvDoneAchievementNumber.startAnimation(anim)
            }
            delay(3000)
            launch(Dispatchers.Main) {
                binding.tvDoneAchievementNumber.clearAnimation()
                android.transition.TransitionManager.beginDelayedTransition(binding.mainContainer)
                binding.tvDoneAchievementNumber.isVisible = false
            }
        }
    }
}