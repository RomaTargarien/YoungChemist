package com.example.youngchemist.ui.screen.main.user

import android.animation.LayoutTransition
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.BottomSheetChangeEmailBinding
import com.example.youngchemist.databinding.BottomSheetChangePasswordBinding
import com.example.youngchemist.databinding.FragmentUserBinding
import com.example.youngchemist.ui.util.ResourceNetwork
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

@AndroidEntryPoint
class UserFragment : Fragment() {

    private val viewModel: UserFragmentViewModel by viewModels()
    private val isKeyBoardOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isPasswordChangedSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private lateinit var binding: FragmentUserBinding
    private lateinit var passwordChangeBinding: BottomSheetChangePasswordBinding
    private lateinit var emailChangeBinding: BottomSheetChangeEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentUserBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        passwordChangeBinding = binding.bmSheetChangePassword
        emailChangeBinding = binding.bmSheetChangeEmail
        passwordChangeBinding.viewModel = viewModel
        binding.bmSheetChangeEmail.viewModel = viewModel
        
        binding.etName.setOnFocusChangeListener { _, b ->
            if (!b) {
                toggleFocus(binding.etName,binding.ivName,true)
            }
        }
        binding.etFamilyName.setOnFocusChangeListener { _, b ->
            if (!b) {
                toggleFocus(binding.etFamilyName,binding.ivFamilyName,true)
            }
        }

        binding.ivName.setOnClickListener {
            toggleFocus(binding.etName,binding.ivName)
        }
        binding.ivFamilyName.setOnClickListener {
            toggleFocus(binding.etFamilyName,binding.ivFamilyName)
        }

        binding.container.setOnClickListener {
            binding.etFamilyName.clearFocus()
            binding.etName.clearFocus()
        }

        viewModel.errorMessageOldPasswordBehavior.observe(viewLifecycleOwner, {
            toggleErrorMessageBehavior(
                passwordChangeBinding.bottomSheetContainer,
                passwordChangeBinding.tvErrorMessageOldPassword,
                it
            )
        })

        viewModel.errorMessageNewPasswordBehavior.observe(viewLifecycleOwner, {
            toggleErrorMessageBehavior(
                passwordChangeBinding.bottomSheetContainer,
                passwordChangeBinding.tvErrorMessageNewPassword,
                it
            )
        })

        viewModel.buttonNextState.observe(viewLifecycleOwner, {
            toggleTextViewBehavior(passwordChangeBinding.tvNext,it)
        })
        viewModel.buttonChangeState.observe(viewLifecycleOwner,{
            toggleTextViewBehavior(passwordChangeBinding.tvChange,it)
        })

        viewModel.reauthenticateResult.observe(viewLifecycleOwner, {
            when (it) {
                is ResourceNetwork.Loading -> {
                    passwordChangeBinding.progressFlask.isVisible = true
                }
                is ResourceNetwork.Success -> {
                    passwordChangeBinding.progressFlask.isVisible = false
                    lifecycleScope.launch {
                        isPasswordChangedSuccess.emit(true)
                    }
                    disableViews(
                        passwordChangeBinding.oldPasswordContainer,
                        passwordChangeBinding.tvNext
                    )
                    enableViews(
                        passwordChangeBinding.newPasswordContainer,
                        passwordChangeBinding.tvChange
                    )
                    passwordChangeBinding.etNewPassword.requestFocus()
                    passwordChangeBinding.etNewPassword.showKeyboard()
                }
                is ResourceNetwork.Error -> {
                    passwordChangeBinding.progressFlask.isVisible = false
                }
            }
        })

        val bottomSheet = passwordChangeBinding.bottomSheetContainer
        val bottomSheetEmail = binding.bmSheetChangeEmail.bottomSheetContainer

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val bottomSheetEmailBehavior = BottomSheetBehavior.from(bottomSheetEmail)

        bottomSheetBehavior.skipCollapsed = true
        bottomSheetEmailBehavior.skipCollapsed = true

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetEmailBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        passwordChangeBinding.newPasswordContainer.alpha = 0.5f
        passwordChangeBinding.tvChange.alpha = 0.5f

        binding.bmSheetChangeEmail.newEmailContainer.alpha = 0.5f
        binding.bmSheetChangeEmail.tvChange.alpha = 0.5f

        binding.changePasswordContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.changeEmailContainer.setOnClickListener {
            bottomSheetEmailBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.bmSheetChangeEmail.ivClose.setOnClickListener {
            bottomSheetEmailBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        passwordChangeBinding.ivClose.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetEmailBehavior.addBottomSheetCallback(bottomSheetCallback)
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        viewModel.isOldPasswordShown.observe(viewLifecycleOwner, {
            togglePasswordVisibility(
                passwordChangeBinding.etOldPassword,
                passwordChangeBinding.ivIsOldPasswordShown,
                it
            )
        })

        KeyboardVisibilityEvent.setEventListener(
            requireActivity(),
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    lifecycleScope.launch {
                        isKeyBoardOpen.emit(isOpen)
                    }
                }
            })

        viewModel.isNewPasswordShown.observe(viewLifecycleOwner, {
            togglePasswordVisibility(
                passwordChangeBinding.etNewPassword,
                passwordChangeBinding.ivIsNewPasswordShown,
                it
            )
        })

        lifecycleScope.launch {
            combine(isKeyBoardOpen, isPasswordChangedSuccess) { isOpen, isSuccess ->
                Pair(isOpen, isSuccess)
            }.collect {
                if (it.first && it.second) {
                    toggleContainerTransition(passwordChangeBinding.mainContainer, 0)
                } else if (!it.first && it.second) {
                    toggleContainerTransition(passwordChangeBinding.mainContainer, 100)
                }
            }
        }
    }

    private fun toggleFocus(editTextField: EditText,imageView: ImageView,clearFocus: Boolean = false) {
        if (clearFocus) {
            editTextField.isEnabled = false
            imageView.setImageResource(R.drawable.ic_baseline_edit_24)
        } else {
            editTextField.isEnabled = true
            editTextField.setSelection(editTextField.text.length)
            editTextField.requestFocus()
            editTextField.showKeyboard()
            imageView.setImageResource(R.drawable.ic_icon_done)
        }

    }

    private fun toggleTextViewBehavior(textView: TextView, enabled: Boolean) {
        textView.alpha = if (enabled) 1.0f else 0.5f
        textView.isEnabled = enabled
    }

    private fun toggleErrorMessageBehavior(
        container: ConstraintLayout,
        errorTextView: TextView,
        errorText: String?
    ) {
        if (errorText != null) {
            TransitionManager.beginDelayedTransition(container)
            errorTextView.isVisible = true
            errorTextView.text = errorText
        } else {
            if (errorTextView.isVisible) {
                TransitionManager.beginDelayedTransition(container)
                errorTextView.isVisible = false
            }
        }
    }

    private fun toggleContainerTransition(container: ConstraintLayout, topMargin: Int) {
        val params = container.layoutParams as ViewGroup.MarginLayoutParams
        (container as ViewGroup).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        params.topMargin = topMargin
        container.layoutParams = params
    }

    private fun togglePasswordVisibility(
        passwordEditText: EditText,
        passwordVisibilityImage: ImageView,
        isVisible: Boolean
    ) {
        if (isVisible) {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            passwordEditText.setSelection(passwordEditText.text.length)
            passwordVisibilityImage.setImageResource(R.drawable.ic_icon_view_password)
        } else {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            passwordEditText.setSelection(passwordEditText.text.length)
            passwordVisibilityImage.setImageResource(R.drawable.ic_icon_hide_password)
        }
    }

    private fun disableViews(container: ConstraintLayout, textView: TextView) {
        container.alpha = 0.5f
        textView.alpha = 0.5f
        textView.isEnabled = false
        container.children.forEach {
            it.alpha = 0.5f
            it.isEnabled = false
        }
    }

    private fun enableViews(container: ConstraintLayout, textView: TextView) {
        container.alpha = 1f
        textView.alpha = 1f
        textView.isEnabled = true
        container.children.forEach {
            it.alpha = 1f
            it.isEnabled = true
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState.equals(BottomSheetBehavior.STATE_EXPANDED)) {
                Log.d("TAG", "exp")
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            viewModel.onBottomSheetStateChanged(slideOffset)
        }

    }

    private fun initializeBottomSheet() {
        val bottomSheet = passwordChangeBinding.bottomSheetContainer
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}