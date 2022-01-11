package com.example.youngchemist.ui.screen.main.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.youngchemist.R
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
        passwordChangeBinding = binding.bmSheetChangePassword
        emailChangeBinding = binding.bmSheetChangeEmail
        emailChangeBinding.lifecycleOwner = viewLifecycleOwner
        passwordChangeBinding.lifecycleOwner = viewLifecycleOwner
        setOnClickListeners()
        setOnFocusChangeListeners()
        initializeBottomSheetChangePassword()
        initializeBottomSheetChangeEmail()
        viewModel.userState.observe(viewLifecycleOwner,{
            when (it) {
                is ResourceNetwork.Loading -> {
                    binding.progressBarName.isVisible = true
                    binding.progressBarSurname.isVisible = true
                    binding.ivFamilyName.apply {
                        isEnabled = false
                        alpha = 0.5f
                    }
                    binding.ivName.apply {
                        isEnabled = false
                        alpha = 0.5f
                    }
                    binding.etFamilyName.isVisible = false
                    binding.etName.isVisible = false
                }
                is ResourceNetwork.Success -> {
                    binding.progressBarName.isVisible = false
                    binding.progressBarSurname.isVisible = false
                    binding.ivFamilyName.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                    binding.ivName.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                    it.data?.let { user->
                        binding.etName.setText(user.name)
                        binding.etFamilyName.setText(user.surname)
                    }
                    binding.etFamilyName.isVisible = true
                    binding.etName.isVisible = true
                }
                is ResourceNetwork.Error -> {
                    binding.progressBarName.isVisible = false
                    binding.progressBarSurname.isVisible = false
                }
            }
        })
    }

    private fun setOnClickListeners() {
        binding.ivName.setOnClickListener {
            toggleFocus(binding.etName, binding.ivName)
        }
        binding.ivFamilyName.setOnClickListener {
            toggleFocus(binding.etFamilyName, binding.ivFamilyName)
        }

        binding.container.setOnClickListener {
            binding.etFamilyName.clearFocus()
            binding.etName.clearFocus()
        }
    }

    private fun setOnFocusChangeListeners() {
        binding.etName.setOnFocusChangeListener { _, b ->
            if (!b) {
                toggleFocus(binding.etName, binding.ivName, true)
            }
        }
        binding.etFamilyName.setOnFocusChangeListener { _, b ->
            if (!b) {
                toggleFocus(binding.etFamilyName, binding.ivFamilyName, true)
            }
        }
    }

    private fun toggleFocus(
        editTextField: EditText,
        imageView: ImageView,
        clearFocus: Boolean = false
    ) {
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