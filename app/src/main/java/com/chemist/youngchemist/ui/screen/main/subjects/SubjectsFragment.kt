package com.chemist.youngchemist.ui.screen.main.subjects

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.FragmentSubjectsBinding
import com.chemist.youngchemist.ui.base.decorators.HorizontalItemDecoration
import com.chemist.youngchemist.ui.util.Resource
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
@AndroidEntryPoint
class SubjectsFragment : Fragment() {

    private val viewModel: SubjectsFragmentViewModel by viewModels()
    private var snackbarNewSubjects: Snackbar? = null
    private lateinit var binding: FragmentSubjectsBinding
    private lateinit var subjectAdapter: SubjectsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentSubjectsBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setUpRecyclerView()
        setOnClickListeners()
        observeUserState()
        observeUserNameState()
        observeSubjectListState()
        observeNewLecturesDownloadedNumber()
    }

    private fun setOnClickListeners() {
        binding.tvTryAgain.setOnClickListener {
            viewModel.tryAgain()
            binding.tvError.isVisible = false
            binding.tvTryAgain.isVisible = false
        }
    }

    private fun setUpRecyclerView() {
        subjectAdapter = SubjectsAdapter()
        binding.rvSubjects.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = subjectAdapter
            addItemDecoration(HorizontalItemDecoration(40))
        }
        subjectAdapter.setOnClickListener { selectedSubject ->
            viewModel.navigateToLecturesListScreen(selectedSubject)
        }
    }

    private fun observeSubjectListState() {
        viewModel.subjectsState.observe(viewLifecycleOwner) {
            when (it) {
                is ResourceNetwork.Loading -> {
                    binding.progressFlask.isVisible = true
                    binding.pbSubject.isVisible = true
                    binding.ivReload.isVisible = false
                }
                is ResourceNetwork.Success -> {
                    binding.progressFlask.isVisible = false
                    it.data?.let {
                        subjectAdapter.submitList(it)
                    }
                }
                is ResourceNetwork.Error -> {
                    binding.progressFlask.isVisible = false
                    binding.tvError.isVisible = true
                    binding.tvTryAgain.isVisible = true
                }
            }
        }
    }

    private fun observeUserState() {
        viewModel.userState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.containerUserState.isVisible = true
                    binding.etSubjectSearch.isEnabled = false
                    subjectAdapter.isClickable = false
                }
                is Resource.Error -> {
                    binding.pbUserState.isVisible = false
                    binding.ivUserState.isVisible = true
                    binding.etSubjectSearch.isEnabled = false
                    subjectAdapter.isClickable = false
                    binding.ivUserState.setImageResource(R.drawable.error)
                }
                is Resource.Success -> {
                    binding.pbUserState.isVisible = false
                    binding.ivUserState.isVisible = true
                    binding.etSubjectSearch.isEnabled = true
                    subjectAdapter.isClickable = true
                    subjectAdapter.notifyDataSetChanged()
                    binding.ivUserState.setImageResource(R.drawable.success)
                }
            }
        }
    }

    private fun observeUserNameState() {
        viewModel.userName.observe(viewLifecycleOwner) {
            when (it) {
                is ResourceNetwork.Loading -> {
                }
                is ResourceNetwork.Success -> {
                    it.data?.let { name ->
                        TransitionManager.beginDelayedTransition(binding.mainContainer)
                        val upperName = name.replaceFirstChar { firstChar -> firstChar.uppercase() }
                        binding.tvUserName.text =
                            resources.getString(R.string.hello_user_name, upperName)
                    }
                }
                is ResourceNetwork.Error -> {
                }
            }
        }
    }

    private fun observeNewLecturesDownloadedNumber() {
        lifecycleScope.launch {
            viewModel.numberOfSubjectsDownloaded.collect { number ->
                val messageText = when (number) {
                    null -> resources.getString(R.string.error_while_loading)
                    0 -> resources.getString(R.string.no_new_subjects)
                    else -> resources.getString(R.string.new_subjects_number, number)
                }
                binding.pbSubject.isVisible = false
                binding.ivReload.isVisible = true
                snackbarNewSubjects =
                    Snackbar.make(binding.mainContainer, messageText, Snackbar.LENGTH_SHORT)
                snackbarNewSubjects?.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        snackbarNewSubjects?.dismiss()
    }
}