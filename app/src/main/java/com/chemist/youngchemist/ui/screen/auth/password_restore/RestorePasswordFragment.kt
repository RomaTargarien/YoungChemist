package com.chemist.youngchemist.ui.screen.auth.password_restore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.FragmentRestorePasswordBinding
import com.chemist.youngchemist.ui.base.AnimationHelper
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.chemist.youngchemist.ui.util.closeKeyBoard
import com.chemist.youngchemist.ui.util.slideUpViews
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview


@FlowPreview
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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        initializeAnimationHelper()
        observeRestorePasswordState()
        observeStateAfterRestoringPassword()
        slideUpViews(
            binding.loginContainer,
            binding.bnRestorePassword
        )
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
    }

    private fun observeRestorePasswordState() {
        viewModel.restorePasswordState.observe(viewLifecycleOwner) {
            val result = it.getContentIfNotHandled()
            result?.let {
                when (result) {
                    is ResourceNetwork.Loading -> {
                        binding.mainContainer.alpha = 0.5f
                        binding.bnRestorePassword.isEnabled = false
                        binding.progressFlask.isVisible = true
                    }
                    is ResourceNetwork.Success -> {
                        binding.progressFlask.isVisible = false
                        binding.bnRestorePassword.isEnabled = true
                        binding.mainContainer.alpha = 1f
                        viewModel.showResultMessage(getString(R.string.go_to_your_email_for_password_changing), false)
                    }
                    is ResourceNetwork.Error -> {
                        binding.mainContainer.alpha = 1f
                        binding.bnRestorePassword.isEnabled = true
                        binding.progressFlask.isVisible = false
                        viewModel.showResultMessage(result.message)
                    }
                }
            }
        }
    }

    private fun observeStateAfterRestoringPassword() {
        viewModel.isResultMessageVisible.observe(viewLifecycleOwner) { result ->
            if (result.second) {
                animationHelper.showMessage(result.first ?: "", result.third)
            } else {
                animationHelper.hideMessage()
            }
        }
    }

    private fun initializeAnimationHelper() {
        animationHelper = AnimationHelper(
            this.requireContext(),
            binding.cvRestorePasswordResult,
            binding.tvResultText,
            binding.ivResultImage,
            binding.tvOoops
        )
    }

    override fun onPause() {
        super.onPause()
        closeKeyBoard()
    }
}