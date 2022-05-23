package com.chemist.youngchemist.ui.screen.main.user.bottom_sheet.change_password

import android.content.Context
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.BottomSheetChangePasswordBinding
import com.chemist.youngchemist.ui.screen.main.user.bottom_sheet.BottomSheetBaseBehavior
import com.chemist.youngchemist.ui.screen.main.user.bottom_sheet.BottomSheetViewModelBase
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.chemist.youngchemist.ui.util.shake
import com.chemist.youngchemist.ui.util.showKeyboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class BottomSheetChangePassword(
    private val bottomSheetChangePasswordBinding: BottomSheetChangePasswordBinding,
    private val context: Context
) : BottomSheetBaseBehavior() {

    val isKeyBoardOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isReathenticationSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var onPasswordHasChanged: ((String) -> Unit)? = null
    override fun setOnDataHasChangedListener(listener: (String) -> Unit) {
        onPasswordHasChanged = listener
    }

    init {
        bottomSheetChangePasswordBinding.lifecycleOwner!!.lifecycleScope.launch {
            combine(isKeyBoardOpen, isReathenticationSuccess) { isOpen, isSuccess ->
                Pair(isOpen, isSuccess)
            }.collect {
                if (it.first && it.second) {
                    toggleContainerTransition(bottomSheetChangePasswordBinding.mainContainer, 0)
                } else if (!it.first && it.second) {
                    toggleContainerTransition(bottomSheetChangePasswordBinding.mainContainer, 100)
                }
            }
        }
    }

    override fun init(viewModel: BottomSheetViewModelBase) {
        bottomSheetChangePasswordBinding.viewModel = viewModel as BottomSheetChangePasswordViewModel
        jumpToInitialState()
    }

    override fun subscribeToObservers() {
        observeOldPasswordChanges()
        observeNewPasswordChanges()
        observeOldPasswordVisibilty()
        observeNewPasswordVisibility()
        observeReathenticationResult()
        observeButtonChangeState()
        observeButtonNextState()
        observeChangePasswordState()
    }

    override fun removeObservers() {
        bottomSheetChangePasswordBinding.viewModel?.let {
            it.apply {
                errorMessageNewPasswordBehavior.removeObservers(bottomSheetChangePasswordBinding.lifecycleOwner!!)
                errorMessageOldPasswordBehavior.removeObservers(bottomSheetChangePasswordBinding.lifecycleOwner!!)
                buttonChangeState.removeObservers(bottomSheetChangePasswordBinding.lifecycleOwner!!)
                buttonNextState.removeObservers(bottomSheetChangePasswordBinding.lifecycleOwner!!)
                isNewPasswordShown.removeObservers(bottomSheetChangePasswordBinding.lifecycleOwner!!)
                isOldPasswordShown.removeObservers(bottomSheetChangePasswordBinding.lifecycleOwner!!)
                reauthenticateResult.removeObservers(bottomSheetChangePasswordBinding.lifecycleOwner!!)
            }
        }
    }

    private fun observeChangePasswordState() {
        bottomSheetChangePasswordBinding.viewModel?.let {
            it.changePasswordState.observe(bottomSheetChangePasswordBinding.lifecycleOwner!!, {
                when (it) {
                    is ResourceNetwork.Loading -> {
                        bottomSheetChangePasswordBinding.progressFlaskPasswordChange.isVisible =
                            true
                    }
                    is ResourceNetwork.Success -> {
                        bottomSheetChangePasswordBinding.progressFlaskPasswordChange.isVisible =
                            false
                        onPasswordHasChanged?.let { change ->
                            change(context.getString(R.string.password_has_successfully_changed))
                        }
                    }
                    is ResourceNetwork.Error -> {
                        bottomSheetChangePasswordBinding.progressFlaskPasswordChange.isVisible =
                            false
                    }
                }
            })
        }
    }

    private fun jumpToInitialState() {
        bottomSheetChangePasswordBinding.apply {
            etOldPassword.apply {
                setText("")
                isEnabled = true
            }
            etNewPassword.apply {
                setText("")
                isEnabled = false
            }
            tvErrorMessageNewPassword.apply {
                setText("")
                isVisible = false
            }
            tvErrorMessageOldPassword.apply {
                setText("")
                isVisible = false
            }
            disableViews(newPasswordContainer, tvChange)
            enableViews(oldPasswordContainer, tvNext)
            toggleContainerTransition(mainContainer, 100)
            progressFlaskReathenticate.isVisible = false
            progressFlaskPasswordChange.isVisible = false
        }
    }

    private fun observeOldPasswordVisibilty() {
        bottomSheetChangePasswordBinding.viewModel?.isOldPasswordShown?.observe(
            bottomSheetChangePasswordBinding.lifecycleOwner!!,
            {
                togglePasswordVisibility(
                    bottomSheetChangePasswordBinding.etOldPassword,
                    bottomSheetChangePasswordBinding.ivIsOldPasswordShown,
                    it
                )
            })
    }

    private fun observeNewPasswordVisibility() {
        bottomSheetChangePasswordBinding.viewModel?.isNewPasswordShown?.observe(
            bottomSheetChangePasswordBinding.lifecycleOwner!!,
            {
                togglePasswordVisibility(
                    bottomSheetChangePasswordBinding.etNewPassword,
                    bottomSheetChangePasswordBinding.ivIsNewPasswordShown,
                    it
                )
            })
    }

    private fun observeOldPasswordChanges() {
        bottomSheetChangePasswordBinding.viewModel?.errorMessageOldPasswordBehavior?.observe(
            bottomSheetChangePasswordBinding.lifecycleOwner!!,
            {
                toggleErrorMessageBehavior(
                    bottomSheetChangePasswordBinding.bottomSheetContainer,
                    bottomSheetChangePasswordBinding.tvErrorMessageOldPassword,
                    it
                )
            })
    }

    private fun observeNewPasswordChanges() {
        bottomSheetChangePasswordBinding.viewModel?.errorMessageNewPasswordBehavior?.observe(
            bottomSheetChangePasswordBinding.lifecycleOwner!!,
            {
                toggleErrorMessageBehavior(
                    bottomSheetChangePasswordBinding.bottomSheetContainer,
                    bottomSheetChangePasswordBinding.tvErrorMessageNewPassword,
                    it
                )
            })
    }

    private fun observeButtonNextState() {
        bottomSheetChangePasswordBinding.viewModel?.buttonNextState?.observe(
            bottomSheetChangePasswordBinding.lifecycleOwner!!,
            {
                toggleTextViewBehavior(bottomSheetChangePasswordBinding.tvNext, it)
            })
    }

    private fun observeButtonChangeState() {
        bottomSheetChangePasswordBinding.viewModel?.buttonChangeState?.observe(
            bottomSheetChangePasswordBinding.lifecycleOwner!!,
            {
                toggleTextViewBehavior(bottomSheetChangePasswordBinding.tvChange, it)
            })
    }

    private fun observeReathenticationResult() {
        bottomSheetChangePasswordBinding.viewModel?.reauthenticateResult?.observe(
            bottomSheetChangePasswordBinding.lifecycleOwner!!,
            {
                when (it) {
                    is ResourceNetwork.Loading -> {
                        bottomSheetChangePasswordBinding.progressFlaskReathenticate.isVisible = true
                    }
                    is ResourceNetwork.Success -> {
                        bottomSheetChangePasswordBinding.progressFlaskReathenticate.isVisible =
                            false
                        bottomSheetChangePasswordBinding.lifecycleOwner!!.lifecycleScope.launch {
                            isReathenticationSuccess.emit(true)
                        }
                        disableViews(
                            bottomSheetChangePasswordBinding.oldPasswordContainer,
                            bottomSheetChangePasswordBinding.tvNext
                        )
                        enableViews(
                            bottomSheetChangePasswordBinding.newPasswordContainer,
                            bottomSheetChangePasswordBinding.tvChange
                        )
                        toggleContainerTransition(bottomSheetChangePasswordBinding.mainContainer, 0)
                        bottomSheetChangePasswordBinding.etNewPassword.requestFocus()
                        bottomSheetChangePasswordBinding.etNewPassword.showKeyboard()
                    }
                    is ResourceNetwork.Error -> {
                        bottomSheetChangePasswordBinding.progressFlaskReathenticate.isVisible =
                            false
                        bottomSheetChangePasswordBinding.etOldPassword.shake().start()
                    }
                }
            })
    }
}