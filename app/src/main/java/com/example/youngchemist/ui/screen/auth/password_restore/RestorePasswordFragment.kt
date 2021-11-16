package com.example.youngchemist.ui.screen.auth.password_restore

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentRestorePasswordBinding
import com.example.youngchemist.ui.base.AnimationHelper
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.closeKeyBoard
import com.example.youngchemist.ui.util.slideUpViews
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestorePasswordFragment : Fragment() {

    private val viewModel: RestorePasswordFragmentViewModel by viewModels()
    private lateinit var binding: FragmentRestorePasswordBinding
    private lateinit var animationHelper: AnimationHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentRestorePasswordBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initializeAnimationHepler()
        slideUpViews(
            binding.loginContainer,
            binding.bnRestorePassword
        )
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

        viewModel.isResultMessageVisible.observe(viewLifecycleOwner, { result ->
            if (result.second) {
                result.first?.let {
                    animationHelper.showMessage(it,result.third)
                }
            } else {
                animationHelper.hideMessage()
            }
        })

        viewModel.restorePasswordState.observe(viewLifecycleOwner, {
            val result = it.getContentIfNotHandled()
            result?.let {
                when (it) {
                    is ResourceNetwork.Loading -> {
                        binding.mainContainer.alpha = 0.5f
                        binding.bnRestorePassword.isEnabled = false
                        binding.progressFlask.isVisible = true
                    }
                    is ResourceNetwork.Success -> {
                        binding.progressFlask.isVisible = false
                        viewModel.showResultMessage(getString(R.string.go_to_your_email_for_password_changing),false)
                    }
                    is ResourceNetwork.Error -> {
                        binding.progressFlask.isVisible = false
                        viewModel.showResultMessage(it.message)
                    }
                }
            }
        })

    }

    private fun initializeAnimationHepler() {
       animationHelper = AnimationHelper(
            this.requireContext(),
            binding.mainContainer,
            binding.cvRestorePasswordResult,
            binding.tvResultText,
            binding.bnRestorePassword,
            binding.ivResultImage,
            binding.tvOoops
        )
    }

    override fun onPause() {
        super.onPause()
        closeKeyBoard()
    }

}