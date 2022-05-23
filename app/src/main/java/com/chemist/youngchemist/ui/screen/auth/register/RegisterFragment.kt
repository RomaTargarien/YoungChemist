package com.chemist.youngchemist.ui.screen.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chemist.youngchemist.databinding.FragmentRegisterBinding
import com.chemist.youngchemist.ui.base.AnimationHelper
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.chemist.youngchemist.ui.util.closeKeyBoard
import com.chemist.youngchemist.ui.util.slideUpViews
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegisterFragmentViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var animationHelper: AnimationHelper

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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        animationHelper = initializeAnimationHepler()
        slideUpViews(
            binding.loginContainer,
            binding.nameContainer,
            binding.surnameContainer,
            binding.passwordContainer,
            binding.repetaedPasswordContainer,
            binding.bnRegister
        )

        viewModel.isErrorMessageVisible.observe(viewLifecycleOwner, {
            if (it.second) {
                it.first?.let { animationHelper.showMessage(it) }
            } else {
                animationHelper.hideMessage()
            }
        })

        viewModel.registerState.observe(viewLifecycleOwner, {
            val result = it.getContentIfNotHandled()
            result?.let {
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
            binding.cvRegisterResult,
            binding.errorText,
            binding.bnRegister
        )
}