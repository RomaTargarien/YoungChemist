package com.example.youngchemist.ui.screen.auth.password_restore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentRestorePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestorePasswordFragment : Fragment() {

    private val viewModel: RestorePasswordFragmentViewModel by viewModels()
    lateinit var binding: FragmentRestorePasswordBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentRestorePasswordBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
        viewModel.restorePasswordButtonEnabled.observe(viewLifecycleOwner, {
            binding.bnRestorePassword.isEnabled = it
            binding.bnRestorePassword.alpha = if (it) 1.0f else 0.7f
        })
        viewModel.errorLoginMessageBehavior.observe(viewLifecycleOwner, {
            TransitionManager.beginDelayedTransition(binding.mainContainer)
            binding.tvLoginErrorMessage.isVisible = it.second
            it.first.let { binding.tvLoginErrorMessage.setText(it) }
        })

    }
}