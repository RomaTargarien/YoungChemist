package com.example.youngchemist.ui.screen.main.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.example.youngchemist.databinding.BottomSheetChangeEmailBinding
import com.example.youngchemist.databinding.BottomSheetChangePasswordBinding
import com.example.youngchemist.databinding.FragmentUserBinding
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.BottomSheetBase
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.change_email.BottomSheetChangeEmail
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.change_password.BottomSheetChangePassword
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.closeKeyBoard
import com.example.youngchemist.ui.util.showKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {

    private val viewModel: UserFragmentViewModel by viewModels()
    private lateinit var binding: FragmentUserBinding
    private lateinit var passwordChangeBinding: BottomSheetChangePasswordBinding
    private lateinit var emailChangeBinding: BottomSheetChangeEmailBinding
    private var bottomSheet: BottomSheetBase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentUserBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        viewModel.getUserInfo()
        passwordChangeBinding = binding.bmSheetChangePassword
        emailChangeBinding = binding.bmSheetChangeEmail
        emailChangeBinding.lifecycleOwner = viewLifecycleOwner
        passwordChangeBinding.lifecycleOwner = viewLifecycleOwner
        setOnClickListeners()
        setOnFocusChangeListeners()
        initializeBottomSheetChangePassword()
        initializeBottomSheetChangeEmail()
        viewModel.errorMessageNewSurnameBehavior.observe(viewLifecycleOwner, {
            toggleErrorMessageBehavior(binding.container, binding.tvErrorMessageSurname, it)
        })
        viewModel.errorMessageNewNameBehavior.observe(viewLifecycleOwner, {
            toggleErrorMessageBehavior(binding.container, binding.tvErrorMessageName, it)
        })
        viewModel.buttonChangeSurnameState.observe(viewLifecycleOwner, {
            binding.ivChangeSurname.apply {
                isEnabled = it
                alpha = if (it) 1f else 0.5f
            }
        })
        viewModel.buttonChangeNameState.observe(viewLifecycleOwner, {
            binding.ivChangeName.apply {
                isEnabled = it
                alpha = if (it) 1f else 0.5f
            }
        })
        binding.ivChangeSurname.setOnClickListener {
            viewModel.changeUserSurname()
            binding.etFamilyName.clearFocus()
        }
        binding.ivChangeName.setOnClickListener {
            viewModel.changeUserName()
            binding.etName.clearFocus()
        }
        viewModel.userState.observe(viewLifecycleOwner, {
            when (it) {
                is ResourceNetwork.Loading -> {
                    binding.progressBarName.isVisible = true
                    binding.progressBarSurname.isVisible = true
                    binding.ivEditSurname.apply {
                        isEnabled = false
                        alpha = 0.5f
                    }
                    binding.ivEditName.apply {
                        isEnabled = false
                        alpha = 0.5f
                    }
                    binding.ivErrorName.isVisible = false
                    binding.ivErrorSurname.isVisible = false
                    binding.etFamilyName.isVisible = false
                    binding.etName.isVisible = false
                    if (binding.ivReloadUserInfo.isVisible) {
                        reloadImageVisibility(false)
                    }
                }
                is ResourceNetwork.Success -> {
                    binding.progressBarName.isVisible = false
                    binding.progressBarSurname.isVisible = false
                    binding.ivEditSurname.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                    binding.ivEditName.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                    it.data?.let { user ->
                        binding.etName.setText(user.name)
                        binding.etFamilyName.setText(user.surname)
                    }
                    binding.etFamilyName.isVisible = true
                    binding.etName.isVisible = true
                }
                is ResourceNetwork.Error -> {
                    binding.progressBarName.isVisible = false
                    binding.progressBarSurname.isVisible = false
                    binding.ivErrorName.isVisible = true
                    binding.ivErrorSurname.isVisible = true
                    reloadImageVisibility(true)
                }
            }
        })
    }

    private fun reloadImageVisibility(isVisible: Boolean) {
        TransitionManager.beginDelayedTransition(binding.container)
        binding.ivReloadUserInfo.isVisible = isVisible
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

    private fun setOnClickListeners() {
        binding.ivEditName.setOnClickListener {
            viewModel.currentUserFlow.value?.let {
                toggleFocus(binding.etName, binding.ivEditName, binding.ivChangeName, it.name)
            }
        }
        binding.ivEditSurname.setOnClickListener {
            viewModel.currentUserFlow.value?.let {
                toggleFocus(
                    binding.etFamilyName,
                    binding.ivEditSurname,
                    binding.ivChangeSurname,
                    it.surname
                )
            }
        }
        binding.container.setOnClickListener {
            binding.etFamilyName.clearFocus()
            binding.etName.clearFocus()
        }
    }

    private fun setOnFocusChangeListeners() {
        binding.etName.setOnFocusChangeListener { _, b ->
            if (!b) {
                viewModel.currentUserFlow.value?.let {
                    toggleFocus(
                        binding.etName,
                        binding.ivEditName,
                        binding.ivChangeName,
                        it.name,
                        true
                    )
                }

            }
        }
        binding.etFamilyName.setOnFocusChangeListener { _, b ->
            if (!b) {
                viewModel.currentUserFlow.value?.let {
                    toggleFocus(
                        binding.etFamilyName,
                        binding.ivEditSurname,
                        binding.ivChangeSurname,
                        it.surname,
                        true
                    )
                }
            }
        }
    }

    private fun toggleFocus(
        editTextField: EditText,
        editImageView: ImageView,
        changeImageView: ImageView,
        userInfo: String,
        clearFocus: Boolean = false
    ) {
        if (clearFocus) {
            editTextField.isEnabled = false
            editTextField.setText(userInfo)
            changeImageView.isVisible = false
            editImageView.isVisible = true
        } else {
            editTextField.isEnabled = true
            editTextField.setSelection(editTextField.text.length)
            editTextField.requestFocus()
            editTextField.showKeyboard()
            changeImageView.isVisible = true
            editImageView.isVisible = false
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState.equals(BottomSheetBehavior.STATE_HIDDEN)) {
                clearBottomSheet()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            viewModel.onBottomSheetStateChanged(slideOffset)
            if (slideOffset < 0) {
                closeKeyBoard()
            }
        }
    }

    private fun initializeBottomSheetChangePassword() {
        val bottomSheetChangePassword = passwordChangeBinding.bottomSheetContainer
        val bottomSheetChangePasswordBehavior = BottomSheetBehavior.from(bottomSheetChangePassword)
        bottomSheetChangePasswordBehavior.skipCollapsed = true
        bottomSheetChangePasswordBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.changePasswordContainer.setOnClickListener {
            bottomSheetChangePasswordBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheet = BottomSheetChangePassword(passwordChangeBinding).apply {
                init(viewModel.createChangePasswordViewModel())
                subscribeToObservers()
                setOnDataHasChangedListener {
                    bottomSheetChangePasswordBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    Snackbar.make(binding.container,it, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
        passwordChangeBinding.ivClose.setOnClickListener {
            bottomSheetChangePasswordBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            closeKeyBoard()
        }
        bottomSheetChangePasswordBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun initializeBottomSheetChangeEmail() {
        val bottomSheetEmail = binding.bmSheetChangeEmail.bottomSheetContainer
        val bottomSheetEmailBehavior = BottomSheetBehavior.from(bottomSheetEmail)
        bottomSheetEmailBehavior.skipCollapsed = true
        bottomSheetEmailBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.changeEmailContainer.setOnClickListener {
            bottomSheetEmailBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheet = BottomSheetChangeEmail(emailChangeBinding).apply {
                init(viewModel.createChangeEmailViewModel())
                subscribeToObservers()
                setOnDataHasChangedListener {
                    bottomSheetEmailBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    Snackbar.make(binding.container,it, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
        binding.bmSheetChangeEmail.ivClose.setOnClickListener {
            bottomSheetEmailBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            closeKeyBoard()
        }
        bottomSheetEmailBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private fun clearBottomSheet() {
        bottomSheet?.removeObservers()
        bottomSheet = null
        viewModel.destroyBottomSheetViewModel()
    }
}