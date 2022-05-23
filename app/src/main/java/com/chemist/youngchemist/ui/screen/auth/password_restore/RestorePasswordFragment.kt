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
        initializeAnimationHepler()
        slideUpViews(
            binding.loginContainer,
            binding.bnRestorePassword
        )
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }

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