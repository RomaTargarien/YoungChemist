package com.example.youngchemist.ui.screen.auth.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentLoginBinding
import com.example.youngchemist.ui.base.AnimationHelper
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.closeKeyBoard
import com.example.youngchemist.ui.util.slideUpViews
import dagger.hilt.android.AndroidEntryPoint


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
        animationHelper = initializeAnimationHepler()
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
        slideUpViews(
            binding.loginContainer,
            binding.passwordContainer,
            binding.tvRestorePassword,
            binding.bnLogin
        )
        binding.viewModel = viewModel
        viewModel.enterButtonEnabled.observe(viewLifecycleOwner, {
            binding.bnLogin.isEnabled = it
            binding.bnLogin.alpha = if (it) 1.0f else 0.7f
        })
        viewModel.errorLoginMessageBehavior.observe(viewLifecycleOwner, {
            TransitionManager.beginDelayedTransition(binding.mainContainer)
            binding.tvLoginErrorMessage.isVisible = it.second
            it.first.let { binding.tvLoginErrorMessage.setText(it) }
        })
        viewModel.errorPasswordMessageBehavior.observe(viewLifecycleOwner, {
            TransitionManager.beginDelayedTransition(binding.mainContainer)
            binding.tvPasswordErrorMessage.isVisible = it.second
            it.first.let { binding.tvPasswordErrorMessage.setText(it) }
        })

        viewModel.isPasswordShown.observe(viewLifecycleOwner, {
            if (it) {
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.etPassword.setSelection(viewModel.passwordText.length)
                binding.ivIsPasswordShown.setImageResource(R.drawable.ic_icon_view_password)
            } else {
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.etPassword.setSelection(viewModel.passwordText.length)
                binding.ivIsPasswordShown.setImageResource(R.drawable.ic_icon_hide_password)
            }
        })

        viewModel.isErrorMessageVisible.observe(viewLifecycleOwner, {
            if (it.second) {
                it.first?.let {
                    animationHelper.showMessage(it)
                }
            } else {
                animationHelper.hideMessage()
            }
        })

        viewModel.loginState.observe(viewLifecycleOwner, {
            closeKeyBoard()
            val result = it.getContentIfNotHandled()
            result?.let {
                Log.d("TAG", it.toString())
                when {
                    it is ResourceNetwork.Loading -> {
                        binding.mainContainer.alpha = 0.5f
                        binding.bnLogin.isEnabled = false
                        binding.progressFlask.isVisible = true
                    }
                    it is ResourceNetwork.Success -> {
                        viewModel.enter()
                    }
                    it is ResourceNetwork.Error -> {
                        binding.progressFlask.isVisible = false
                        viewModel.showMessage(it.message)
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        closeKeyBoard()
    }

    private fun initializeAnimationHepler() =
        AnimationHelper(
            this.requireContext(),
            binding.mainContainer,
            binding.cvLoginResult,
            binding.tvErrorText,
            binding.bnLogin
        )
}
