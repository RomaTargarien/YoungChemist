package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
import com.example.youngchemist.model.Test
import com.example.youngchemist.service.AchievementService
import com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list.dialogs.StartTestDialogFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.tests.dialogs.TestNoSaveDialogFragment
import com.example.youngchemist.ui.util.BitmapUtils
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.closeKeyBoard
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LecturesListFragment : Fragment() {

    private lateinit var binding: FragmentLecturesListBinding
    private val viewModel: LecturesListViewModel by viewModels()
    private var subject: Subject? = null
    private var mService: AchievementService? = null
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AchievementService.LocalBinder
            mService = binder.getService()
            showUnViewedAchievementNumber()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {}
    }

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
            loadImage(it)
        }
        initializeRecyclerView()
        setOnClickListeners()
        checkServiceConnection()
        observeUserProgressCounts()
        viewModel.lecturesUiState.observe(viewLifecycleOwner, {
            when (it) {
                is ResourceNetwork.Success -> {
                    binding.progressFlask.isVisible = false
                    it.data?.let {
                        lecturesListAdapter.submitList(it)
                    }
                }
                is ResourceNetwork.Error -> {
                    binding.progressFlask.isVisible = false
                }
                is ResourceNetwork.Loading -> {
                    binding.progressFlask.isVisible = true
                }
            }
        })
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
    }

    private fun showStartTestDialogFragment(test: Test) {
        val startTestDialogFragment = StartTestDialogFragment(viewModel,test)
        startTestDialogFragment.show(requireActivity().supportFragmentManager, "dialog")
    }

    private fun checkServiceConnection() {
        if (mService == null) {
            bindToService()
        } else {
            showUnViewedAchievementNumber()
        }
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
        viewModel.passedTestsCount.observe(viewLifecycleOwner, {
            binding.pbDoneTests.apply {
                progressMax = it.first.toFloat()
                setProgressWithAnimation(it.second.toFloat(), 1800)
                binding.tvTestsDone.text = it.second.toString()
            }
        })
        viewModel.readenLecturesCount.observe(viewLifecycleOwner, {
            binding.pbReadLectures.apply {
                progressMax = it.first.toFloat()
                setProgressWithAnimation(it.second.toFloat(), 1800)
            }
            binding.tvReadLectures.text = it.second.toString()
        })
    }

    private fun setOnClickListeners() {
        lecturesListAdapter.setOnClickListener {
            viewModel.navigateToLectureScreen(it)
        }
        lecturesListAdapter.setOnBeginTestListener {
            showStartTestDialogFragment(it)
        }
        lecturesListAdapter.setOnLectureIsUnlockedListener {
            closeKeyBoard()
            viewModel.navigateToLectureScreen(it)
        }
    }

    private fun loadImage(item: Subject) {
        if (item.iconByteArray.isEmpty()) {
            Picasso.get()
                .load(item.icon_url)
                .placeholder(R.drawable.ic_icon_happy_flask)
                .into(binding.ivSubject)
        } else {
            val bitmap = BitmapUtils.convertCompressedByteArrayToBitmap(item.iconByteArray)
            binding.ivSubject.setImageBitmap(bitmap)
        }
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