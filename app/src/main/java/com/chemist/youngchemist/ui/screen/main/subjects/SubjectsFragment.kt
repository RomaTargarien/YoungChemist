package com.chemist.youngchemist.ui.screen.main.subjects

import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.FragmentSubjectsBinding
import com.chemist.youngchemist.ui.util.Resource
import com.chemist.youngchemist.ui.util.ResourceNetwork
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview


@FlowPreview
@AndroidEntryPoint
class SubjectsFragment : Fragment() {

    private lateinit var binding: FragmentSubjectsBinding
    private val viewModel: SubjectsFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentSubjectsBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SubjectsAdapter()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvSubjects.layoutManager = GridLayoutManager(this.requireContext(),3)
        binding.rvSubjects.adapter = adapter
        adapter.setOnClickListener {
            viewModel.navigateToLecturesListScreen(it)
        }
        viewModel.userState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.containerUserState.isVisible = true
                    binding.etSubjectSearch.isEnabled = false
                    adapter.isClickable = false
                }
                is Resource.Error -> {
                    binding.pbUserState.isVisible = false
                    binding.ivUserState.isVisible = true
                    binding.etSubjectSearch.isEnabled = false
                    adapter.isClickable = false
                    binding.ivUserState.setImageResource(R.drawable.error)
                }
                is Resource.Success -> {
                    binding.pbUserState.isVisible = false
                    binding.ivUserState.isVisible = true
                    binding.etSubjectSearch.isEnabled = true
                    adapter.isClickable = true
                    adapter.notifyDataSetChanged()
                    binding.ivUserState.setImageResource(R.drawable.success)
                }
            }
        }

        binding.tvTryAgain.setOnClickListener {
            viewModel.tryAgain()
            binding.tvError.isVisible = false
            binding.tvTryAgain.isVisible = false
        }

        viewModel.userName.observe(viewLifecycleOwner) {
            when (it) {
                is ResourceNetwork.Loading -> {

                }
                is ResourceNetwork.Success -> {
                    it.data?.let {
                        TransitionManager.beginDelayedTransition(binding.mainContainer)
                        it.replaceFirstChar { it.uppercase() }.also {
                            binding.tvUserName.text =
                                resources.getString(R.string.hello_user_name, it)
                        }
                    }

                }
                is ResourceNetwork.Error -> {

                }
            }
        }

        viewModel.subjectsState.observe(viewLifecycleOwner) {
            when (it) {
                is ResourceNetwork.Loading -> {
                    binding.progressFlask.isVisible = true
                }
                is ResourceNetwork.Success -> {
                    binding.progressFlask.isVisible = false
                    it.data?.let {
                        adapter.submitList(it)
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

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}