package com.example.youngchemist.ui.screen.main.user.bottom_sheet.change_email

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.ui.base.validation.ValidationImpl
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.BottomSheetViewModelBase
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.TextInputResource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.EmptyCoroutineContext

class BottomSheetChangeEmailViewModel(
    private val loginValidation: ValidationImpl.LoginValidation,
    private val passwordValidation: ValidationImpl.PasswordValidation,
    private val authRepository: AuthRepository
) : BottomSheetViewModelBase {

    private val _emailChangeState: MutableLiveData<ResourceNetwork<String>> = MutableLiveData<ResourceNetwork<String>>()
    val emailChangeState: LiveData<ResourceNetwork<String>> = _emailChangeState

    private val _reauthenticateResult: MutableLiveData<ResourceNetwork<String>> = MutableLiveData()
    val reauthenticateResult: LiveData<ResourceNetwork<String>> = _reauthenticateResult

    private val _isPasswordShown: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPasswordShown: LiveData<Boolean> = _isPasswordShown

    val passwordState: MutableStateFlow<String> = MutableStateFlow("")
    val newEmailState: MutableStateFlow<String> = MutableStateFlow("")

    private var passwordJob: Job? = null
    private var newEmailJob: Job? = null

    private val _errorMessagePasswordBehavior: MutableLiveData<String?> = MutableLiveData()
    val errorMessagePasswordBehavior: LiveData<String?> = _errorMessagePasswordBehavior

    private val _errorMessageNewEmailBehavior: MutableLiveData<String?> = MutableLiveData()
    val errorMessageNewEmailBehavior: LiveData<String?> = _errorMessageNewEmailBehavior

    private val _buttonNextState: MutableLiveData<Boolean> = MutableLiveData(false)
    val buttonNextState: LiveData<Boolean> = _buttonNextState

    private val _buttonChangeState: MutableLiveData<Boolean> = MutableLiveData(false)
    val buttonChangeState: LiveData<Boolean> = _buttonChangeState

    fun reauthenticate() {
        CoroutineScope(EmptyCoroutineContext).launch {
            _reauthenticateResult.postValue(ResourceNetwork.Loading())
            val result = authRepository.reauthenticate(passwordState.value)
            _reauthenticateResult.postValue(result)
        }
    }

    fun changeEmail() {
        CoroutineScope(EmptyCoroutineContext).launch {
            val result = authRepository.updateEmail(newEmailState.value)
            _emailChangeState.postValue(result)
        }
    }

    fun togglePasswordVisibility() {
        _isPasswordShown.value?.let {
            _isPasswordShown.postValue(!it)
        }
    }

    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        passwordJob?.cancel()
        _errorMessagePasswordBehavior.postValue(null)
        _buttonNextState.postValue(false)
        passwordJob = CoroutineScope(EmptyCoroutineContext).launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                if (it is TextInputResource.SuccessInput) {
                    _buttonNextState.postValue(true)
                    passwordState.emit(s.toString())
                } else {
                    _errorMessagePasswordBehavior.postValue(it.message)
                }
            }
        }
    }

    fun onNewEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        newEmailJob?.cancel()
        _errorMessageNewEmailBehavior.postValue(null)
        _buttonChangeState.postValue(false)
        newEmailJob = CoroutineScope(EmptyCoroutineContext).launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(loginValidation.validate(s.toString()))
            }.collect {
                if (it is TextInputResource.SuccessInput) {
                    _buttonChangeState.postValue(true)
                    newEmailState.emit(s.toString())
                } else {
                    _errorMessageNewEmailBehavior.postValue(it.message)
                }
            }
        }
    }
}