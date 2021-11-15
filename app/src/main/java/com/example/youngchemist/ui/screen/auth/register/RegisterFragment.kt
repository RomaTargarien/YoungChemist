package com.example.youngchemist.ui.screen.auth.register

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentRegisterBinding
import com.example.youngchemist.ui.base.AnimationHelper
import com.example.youngchemist.ui.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegisterFragmentViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding
    private val animationHelper: AnimationHelper by lazy(::initializeAnimationHepler)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentRegisterBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
        binding.viewModel = viewModel

        slideUpViews(
            binding.loginContainer,
            binding.familyNameContainer,
            binding.passwordContainer,
            binding.repetaedPasswordContainer,
            binding.bnRegister
        )

        viewModel.registerButtonEnabled.observe(viewLifecycleOwner, {
            binding.bnRegister.isEnabled = it
            binding.bnRegister.alpha = if (it) 1.0f else 0.7f
        })
        viewModel.errorLoginMessageBehavior.observe(viewLifecycleOwner, {
            TransitionManager.beginDelayedTransition(binding.mainContainer)
            binding.tvLoginErrorMessage.isVisible = it.second
            it.first.let { binding.tvLoginErrorMessage.setText(it) }
        })
        viewModel.errorSurnameMessageBehavior.observe(viewLifecycleOwner, {
            TransitionManager.beginDelayedTransition(binding.mainContainer)
            binding.tvSurnameErrorMessage.isVisible = it.second
            it.first.let { binding.tvSurnameErrorMessage.setText(it) }
        })
        viewModel.errorPasswordMessageBehavior.observe(viewLifecycleOwner, {
            TransitionManager.beginDelayedTransition(binding.mainContainer)
            binding.tvPasswordErrorMessage.isVisible = it.second
            it.first.let { binding.tvPasswordErrorMessage.setText(it) }
        })
        viewModel.errorRepeatedPasswordMessageBehavior.observe(viewLifecycleOwner, {
            TransitionManager.beginDelayedTransition(binding.mainContainer)
            binding.tvRepeatedPasswordErrorMessage.isVisible = it.second
            it.first.let { binding.tvRepeatedPasswordErrorMessage.setText(it) }
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

        viewModel.isRepeatedPasswordShown.observe(viewLifecycleOwner, {
            if (it) {
                binding.etRepeatedPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.etRepeatedPassword.setSelection(viewModel.repeatedPasswordText.length)
                binding.ivIsRepeatedPasswordShown.setImageResource(R.drawable.ic_icon_view_password)
            } else {
                binding.etRepeatedPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.etRepeatedPassword.setSelection(viewModel.repeatedPasswordText.length)
                binding.ivIsRepeatedPasswordShown.setImageResource(R.drawable.ic_icon_hide_password)
            }
        })

        viewModel.isErrorMessageVisible.observe(viewLifecycleOwner, {
            if (it.second) {
                it.first?.let { animationHelper.showErrorMessage(it) }
            } else {
                animationHelper.hideErrorMessage()
            }
        })

        viewModel.registerState.observe(viewLifecycleOwner, {
            when {
                it is ResourceNetwork.Loading -> {
                    binding.mainContainer.alpha = 0.5f
                    binding.bnRegister.isEnabled = false
                    binding.progressFlask.isVisible = true
                }
                it is ResourceNetwork.Success -> {
                    viewModel.enter()
                }
                it is ResourceNetwork.Error -> {
                    binding.progressFlask.isVisible = false
                    viewModel.showError(it.message)
                }
            }
        })
    }

    private fun initializeAnimationHepler() =
        AnimationHelper(
            this.requireContext(),
            binding.mainContainer,
            binding.cvRegisterResult,
            binding.errorText,
            binding.bnRegister
        )
}