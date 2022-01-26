package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

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
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentLecturesListBinding
import com.example.youngchemist.model.Subject
import com.example.youngchemist.service.AchievementService
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.tests.dialogs.TestNoSaveDialogFragment
import com.example.youngchemist.ui.util.BitmapUtils
import com.example.youngchemist.ui.util.ResourceNetwork
import dagger.hilt.android.AndroidEntryPoint

private const val SUBJECT = "param1"

@AndroidEntryPoint
class LecturesListFragment : Fragment() {

    private lateinit var binding: FragmentLecturesListBinding
    private var param1: Subject? = null
    private val viewModel: LecturesListViewModel by viewModels()
    private val adapter = LecturesListAdapter()
    private var mService: AchievementService? = null
    private var mBound: Boolean = false
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AchievementService.LocalBinder
            mService = binder.getService()
            showUnViewedAchievementNumber()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(SUBJECT)
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
        binding.rvLectures.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvLectures.adapter = adapter
        param1?.let {
            viewModel.getData(it.collectionId)
        }
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
        param1?.let {
            val bitmap = BitmapUtils.convertCompressedByteArrayToBitmap(it.iconByteArray)
            binding.ivSubject.setImageBitmap(bitmap)
            binding.tvSubjectTitle.text = it.title
        }
        adapter.setOnClickListener {
            viewModel.navigateToLectureScreen(it)
        }
        adapter.setOnBeginTestListener {
            viewModel.navigateToTestScreen(it)
        }
        adapter.setOnLectureIsUnlockedListener {
            it.userProgress?.let { viewModel.saveProgress(it) }
            viewModel.navigateToLectureScreen(it)
        }

        if (mService == null) {
            bindToService()
        } else {
            showUnViewedAchievementNumber()
        }

        viewModel.lecturesUi.observe(viewLifecycleOwner, {
            var allAmountOfTests = 0
            var doneTest = 0
            val allAmountsOfLectures = it.size
            var readLectures = 0
            adapter.submitList(it)
            it.forEach { lecture ->
                lecture.test?.let {
                    allAmountOfTests++
                    if (!lecture.isTestEnabled) {
                        doneTest++
                    }
                }
                lecture.userProgress?.let {
                    if (it.lastReadenPage.equals(lecture.data.size)) {
                        readLectures++
                    }
                }
            }
            binding.pbDoneTests.apply {
                progressMax = allAmountOfTests.toFloat()
                setProgressWithAnimation(doneTest.toFloat(), 1800)
                progressBarColor = resources.getColor(R.color.teal_200)
            }
            binding.pbReadLectures.apply {
                progressMax = allAmountsOfLectures.toFloat()
                setProgressWithAnimation(readLectures.toFloat(), 1800)
                progressBarColor = resources.getColor(R.color.teal_200)
            }
            binding.tvReadLectures.text = readLectures.toString()
            binding.tvTestsDone.text = doneTest.toString()
        })

        viewModel.lecturesListState.observe(viewLifecycleOwner, {
            when (it) {
                is ResourceNetwork.Loading -> {
                    binding.progressFlask.isVisible = true
                }
                is ResourceNetwork.Success -> {
                    binding.progressFlask.isVisible = false
                }
                is ResourceNetwork.Error -> {

                }
            }
        })
    }

    private fun showUnViewedAchievementNumber() {
        mService?.doneAchievements?.observe(viewLifecycleOwner, {
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
        })
    }

    private fun bindToService() {
        Intent(requireContext(), AchievementService::class.java).also { intent ->
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(subject: Subject) =
            LecturesListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SUBJECT, subject)
                }
            }
    }
}