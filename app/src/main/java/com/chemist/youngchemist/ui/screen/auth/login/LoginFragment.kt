package com.chemist.youngchemist.ui.screen.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chemist.youngchemist.databinding.FragmentLoginBinding
import com.chemist.youngchemist.ui.base.AnimationHelper
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.chemist.youngchemist.ui.util.closeKeyBoard
import com.chemist.youngchemist.ui.util.slideUpViews
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginFragmnetViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var animationHelper: AnimationHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentLoginBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        initializeAnimationHelper()
        observeErrorWhileLogin()
        observeLoginState()
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
        slideUpViews(
            binding.loginContainer,
            binding.passwordContainer,
            binding.tvRestorePassword,
            binding.bnLogin
        )
    }

    override fun onPause() {
        super.onPause()
        closeKeyBoard()
    }

    private fun observeErrorWhileLogin() {
        viewModel.isErrorMessageVisible.observe(viewLifecycleOwner) {
            if (it.second) {
                animationHelper.showMessage(it.first ?: "")
            } else {
                animationHelper.hideMessage()
            }
        }
    }

    private fun observeLoginState() {
        viewModel.loginState.observe(viewLifecycleOwner) {
            closeKeyBoard()
            val result = it.getContentIfNotHandled()
            result?.let {
                when (result) {
                    is ResourceNetwork.Loading -> {
                        binding.mainContainer.alpha = 0.5f
                        binding.bnLogin.isEnabled = false
                        binding.progressFlask.isVisible = true
                    }
                    is ResourceNetwork.Success -> {
                        viewModel.enter()
                    }
                    is ResourceNetwork.Error -> {
                        binding.mainContainer.alpha = 1f
                        binding.bnLogin.isEnabled = true
                        binding.progressFlask.isVisible = false
                        viewModel.showMessage(result.message)
                    }
                }
            }
        }
    }

    private fun initializeAnimationHelper() {
        animationHelper = AnimationHelper(
            this.requireContext(),
            binding.cvLoginResult,
            binding.tvErrorText
        )
    }
}
