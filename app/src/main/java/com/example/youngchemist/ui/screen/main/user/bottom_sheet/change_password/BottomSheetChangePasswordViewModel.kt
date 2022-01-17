package com.example.youngchemist.ui.screen.main.user.bottom_sheet.change_password

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.ui.base.validation.ValidationImpl
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.BottomSheetViewModelBase
import com.example.youngchemist.ui.util.Constants.TEST_USER
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.EmptyCoroutineContext

class BottomSheetChangePasswordViewModel(
    private val passwordValidation: ValidationImpl.PasswordValidation,
    private val authRepository: AuthRepository
): BottomSheetViewModelBase {

    private val _reauthenticateResult: MutableLiveData<ResourceNetwork<String>> = MutableLiveData()
    val reauthenticateResult: LiveData<ResourceNetwork<String>> = _reauthenticateResult

    private val _isOldPasswordShown: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOldPasswordShown: LiveData<Boolean> = _isOldPasswordShown

    private val _isNewPasswordShown: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNewPasswordShown: LiveData<Boolean> = _isNewPasswordShown

    val oldPasswordFlow: MutableStateFlow<String> = MutableStateFlow("")
    val newPasswordFlow: MutableStateFlow<String> = MutableStateFlow("")

    private var oldPasswordJob: Job? = null
    private var newPasswordJob: Job? = null

    private val _errorMessageNewPasswordBehavior: MutableLiveData<String?> = MutableLiveData()
    val errorMessageNewPasswordBehavior: LiveData<String?> = _errorMessageNewPasswordBehavior

    private val _errorMessageOldPasswordBehavior: MutableLiveData<String?> = MutableLiveData()
    val errorMessageOldPasswordBehavior: LiveData<String?> = _errorMessageOldPasswordBehavior

    private val _buttonNextState: MutableLiveData<Boolean> = MutableLiveData(false)
    val buttonNextState: LiveData<Boolean> = _buttonNextState

    private val _buttonChangeState: MutableLiveData<Boolean> = MutableLiveData(false)
    val buttonChangeState: LiveData<Boolean> = _buttonChangeState

    private val _changePasswordState: MutableLiveData<ResourceNetwork<String>> = MutableLiveData<ResourceNetwork<String>>()
    val changePasswordState: LiveData<ResourceNetwork<String>> = _changePasswordState


    fun reauthenticate() {
        CoroutineScope(EmptyCoroutineContext).launch {
            _reauthenticateResult.postValue(ResourceNetwork.Loading())
            val result = authRepository.reauthenticate(oldPasswordFlow.value)
            _reauthenticateResult.postValue(result)
        }
    }

    fun changePassword() {
        CoroutineScope(EmptyCoroutineContext).launch {
            val result = authRepository.updatePassword(newPasswordFlow.value)
            _changePasswordState.postValue(result)
        }
    }

    fun toggleOldPasswordVisibility() {
        _isOldPasswordShown.value?.let {
            _isOldPasswordShown.postValue(!it)
        }
    }

    fun toggleNewPasswordVisibility() {
        _isNewPasswordShown.value?.let {
            _isNewPasswordShown.postValue(!it)
        }
    }

    fun onOldPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        oldPasswordJob?.cancel()
        _errorMessageOldPasswordBehavior.postValue(null)
        _buttonNextState.postValue(false)
        oldPasswordJob = CoroutineScope(EmptyCoroutineContext).launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                if (it is Resource.Success) {
                    _buttonNextState.postValue(true)
                    oldPasswordFlow.emit(s.toString())
                } else {
                    _errorMessageOldPasswordBehavior.postValue(it.message)
                }
            }
        }
    }

    fun onNewPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        newPasswordJob?.cancel()
        _errorMessageNewPasswordBehavior.postValue(null)
        _buttonChangeState.postValue(false)
        newPasswordJob = CoroutineScope(EmptyCoroutineContext).launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                if (it is Resource.Success) {
                    _buttonChangeState.postValue(true)
                    newPasswordFlow.emit(s.toString())
                } else {
                    _errorMessageNewPasswordBehavior.postValue(it.message)
                }
            }
        }
    }
}