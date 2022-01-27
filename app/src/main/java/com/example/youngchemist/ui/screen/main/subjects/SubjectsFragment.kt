package com.example.youngchemist.ui.screen.main.subjects

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentSubjectsBinding
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import dagger.hilt.android.AndroidEntryPoint


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
        binding.ivSearch.setOnClickListener {
            binding.etSubjectSearch.isEnabled = true
            binding.etSubjectSearch.requestFocus()
            binding.etSubjectSearch.showKeyboard()
        }

        binding.rvSubjects.layoutManager = GridLayoutManager(this.requireContext(),3)
        binding.rvSubjects.adapter = adapter
        adapter.setOnClickListener {
            viewModel.navigateToLecturesListScreen(it)
        }
        viewModel.userState.observe(viewLifecycleOwner,{
            when (it) {
                is Resource.Loading -> {
                    binding.containerUserState.isVisible = true
                    adapter.isClickable = false
                }
                is Resource.Error -> {
                    binding.pbUserState.isVisible = false
                    binding.ivUserState.isVisible = true
                    adapter.isClickable = false
                    binding.ivUserState.setImageResource(R.drawable.error)
                }
                is Resource.Success -> {
                    binding.pbUserState.isVisible = false
                    binding.ivUserState.isVisible = true
                    adapter.isClickable = true
                    adapter.notifyDataSetChanged()
                    binding.ivUserState.setImageResource(R.drawable.success)
                }
            }
        })

        binding.tvTryAgain.setOnClickListener {
            viewModel.tryAgain()
            binding.tvError.isVisible = false
            binding.tvTryAgain.isVisible = false
        }

        viewModel.subjectsState.observe(viewLifecycleOwner,{
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
        })
    }

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}