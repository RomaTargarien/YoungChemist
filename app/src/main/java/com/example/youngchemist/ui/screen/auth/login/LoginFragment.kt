package com.example.youngchemist.ui.screen.auth.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.example.youngchemist.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element

import android.renderscript.ScriptIntrinsicBlur

import android.renderscript.RenderScript
import android.view.ViewTreeObserver


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginFragmnetViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentLoginBinding.inflate(inflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        viewModel.enterButtonEnabled.observe(viewLifecycleOwner, {
            binding.bnEnter.isEnabled = it
            binding.bnEnter.alpha = if (it) 1.0f else 0.7f
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


    }
}
