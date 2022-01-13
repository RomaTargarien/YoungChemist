package com.example.youngchemist.ui.screen.main.user.bottom_sheet.change_email

import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.youngchemist.databinding.BottomSheetChangeEmailBinding
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.BottomSheetBaseBehavior
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.BottomSheetViewModelBase
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.shake
import com.example.youngchemist.ui.util.showKeyboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BottomSheetChangeEmail(private val bottomSheetChangeEmailBinding: BottomSheetChangeEmailBinding) :
    BottomSheetBaseBehavior() {

    val isKeyBoardOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isReathenticationSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var onEmailHasChanged: ((String) -> Unit)? = null
    override fun setOnDataHasChangedListener(listener: (String) -> Unit) {
        onEmailHasChanged = listener
    }

    init {
        bottomSheetChangeEmailBinding.lifecycleOwner!!.lifecycleScope.launch {
            combine(isKeyBoardOpen, isReathenticationSuccess) { isOpen, isSuccess ->
                Pair(isOpen, isSuccess)
            }.collect {
                if (it.first && it.second) {
                    toggleContainerTransition(bottomSheetChangeEmailBinding.mainContainer, 0)
                } else if (!it.first && it.second) {
                    toggleContainerTransition(bottomSheetChangeEmailBinding.mainContainer, 100)
                }
            }
        }
    }

    override fun init(viewModel: BottomSheetViewModelBase) {
        bottomSheetChangeEmailBinding.viewModel = viewModel as BottomSheetChangeEmailViewModel
        jumpToInitialState()
    }

    override fun subscribeToObservers() {
        observeButtonChangeState()
        observeButtonNextState()
        observePasswordVisibilty()
        observePasswordChanges()
        observeEmailChanges()
        observeReathenticationResult()
    }

    override fun removeObservers() {
        bottomSheetChangeEmailBinding.viewModel?.let {
            it.apply {
                errorMessagePasswordBehavior.removeObservers(bottomSheetChangeEmailBinding.lifecycleOwner!!)
                errorMessageNewEmailBehavior.removeObservers(bottomSheetChangeEmailBinding.lifecycleOwner!!)
                buttonChangeState.removeObservers(bottomSheetChangeEmailBinding.lifecycleOwner!!)
                buttonNextState.removeObservers(bottomSheetChangeEmailBinding.lifecycleOwner!!)
                isPasswordShown.removeObservers(bottomSheetChangeEmailBinding.lifecycleOwner!!)
                reauthenticateResult.removeObservers(bottomSheetChangeEmailBinding.lifecycleOwner!!)
            }
        }
    }

    private fun observePasswordVisibilty() {
        bottomSheetChangeEmailBinding.viewModel?.isPasswordShown?.observe(
            bottomSheetChangeEmailBinding.lifecycleOwner!!,
            {
                togglePasswordVisibility(
                    bottomSheetChangeEmailBinding.etPassword,
                    bottomSheetChangeEmailBinding.ivIsPasswordShown,
                    it
                )
            })
    }

    private fun observePasswordChanges() {
        bottomSheetChangeEmailBinding.viewModel?.errorMessagePasswordBehavior?.observe(
            bottomSheetChangeEmailBinding.lifecycleOwner!!,
            {
                toggleErrorMessageBehavior(
                    bottomSheetChangeEmailBinding.bottomSheetContainer,
                    bottomSheetChangeEmailBinding.tvErrorMessagePassword,
                    it
                )
            })
    }

    private fun observeEmailChanges() {
        bottomSheetChangeEmailBinding.viewModel?.errorMessageNewEmailBehavior?.observe(
            bottomSheetChangeEmailBinding.lifecycleOwner!!,
            {
                toggleErrorMessageBehavior(
                    bottomSheetChangeEmailBinding.bottomSheetContainer,
                    bottomSheetChangeEmailBinding.tvErrorMessageNewEmail,
                    it
                )
            })
    }

    private fun observeButtonNextState() {
        bottomSheetChangeEmailBinding.viewModel?.buttonNextState?.observe(
            bottomSheetChangeEmailBinding.lifecycleOwner!!,
            {
                toggleTextViewBehavior(bottomSheetChangeEmailBinding.tvNext, it)
            })
    }

    private fun observeButtonChangeState() {
        bottomSheetChangeEmailBinding.viewModel?.buttonChangeState?.observe(
            bottomSheetChangeEmailBinding.lifecycleOwner!!,
            {
                toggleTextViewBehavior(bottomSheetChangeEmailBinding.tvChange, it)
            })
    }

    private fun observeReathenticationResult() {
        bottomSheetChangeEmailBinding.viewModel?.reauthenticateResult?.observe(
            bottomSheetChangeEmailBinding.lifecycleOwner!!,
            {
                when (it) {
                    is ResourceNetwork.Loading -> {
                        bottomSheetChangeEmailBinding.progressFlask.isVisible = true
                    }
                    is ResourceNetwork.Success -> {
                        bottomSheetChangeEmailBinding.progressFlask.isVisible = false
                        bottomSheetChangeEmailBinding.lifecycleOwner!!.lifecycleScope.launch {
                            isReathenticationSuccess.emit(true)
                        }
                        disableViews(
                            bottomSheetChangeEmailBinding.passwordContainer,
                            bottomSheetChangeEmailBinding.tvNext
                        )
                        enableViews(
                            bottomSheetChangeEmailBinding.newEmailContainer,
                            bottomSheetChangeEmailBinding.tvChange
                        )
                        toggleContainerTransition(bottomSheetChangeEmailBinding.mainContainer,0)
                        bottomSheetChangeEmailBinding.etNewEmail.requestFocus()
                        bottomSheetChangeEmailBinding.etNewEmail.showKeyboard()
                    }
                    is ResourceNetwork.Error -> {
                        bottomSheetChangeEmailBinding.progressFlask.isVisible = false
                        bottomSheetChangeEmailBinding.etPassword.shake().start()
                    }
                }
            })
    }

    private fun jumpToInitialState() {
        bottomSheetChangeEmailBinding.apply {
            etPassword.apply {
                setText("")
                isEnabled = true
            }
            etNewEmail.apply {
                setText("")
                isEnabled = false
            }
            tvErrorMessagePassword.apply {
                setText("")
                isVisible = false
            }
            tvErrorMessageNewEmail.apply {
                setText("")
                isVisible = false
            }
            disableViews(newEmailContainer, tvChange)
            enableViews(passwordContainer, tvNext)
            toggleContainerTransition(mainContainer, 100)
            progressFlask.isVisible = false
        }
    }
}