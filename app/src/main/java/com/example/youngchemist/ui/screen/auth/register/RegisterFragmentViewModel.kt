package com.example.youngchemist.ui.screen.auth.register

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.R
import com.example.youngchemist.model.AuthValidationResults
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Resource
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.example.youngchemist.ui.base.validation.ValidationImpl.SurnameValidation
import com.example.youngchemist.ui.base.validation.ValidationImpl.PasswordValidation
import com.example.youngchemist.ui.base.validation.ValidationImpl.LoginValidation
import com.example.youngchemist.ui.util.ResourceNetwork
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterFragmentViewModel @Inject constructor(
    private val router: Router,
    private val loginValidation: LoginValidation,
    private val passwordValidation: PasswordValidation,
    private val surnameValidation: SurnameValidation,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var loginText = ""
    var surnameText = ""
    var passwordText = ""
    var repeatedPasswordText = ""

    private var loginJob: Job? = null
    private var surnameJob: Job? = null
    private var passwordJob: Job? = null
    private var repeatesPasswordJob: Job? = null

    private val stateLogin = MutableStateFlow<Resource<String>>(Resource.Error(""))
    private val stateSurname = MutableStateFlow<Resource<String>>(Resource.Error(""))
    private val statePassword = MutableStateFlow<Resource<String>>(Resource.Error(""))
    private val stateRepeatedPassword = MutableStateFlow<Resource<String>>(Resource.Error(""))

    private val _errorLoginMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData<Pair<String?, Boolean>>()
    val errorLoginMessageBehavior: LiveData<Pair<String?, Boolean>> = _errorLoginMessageBehavior

    private val _errorSurnameMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData<Pair<String?, Boolean>>()
    val errorSurnameMessageBehavior: LiveData<Pair<String?, Boolean>> = _errorSurnameMessageBehavior

    private val _errorPasswordMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData<Pair<String?, Boolean>>()
    val errorPasswordMessageBehavior: LiveData<Pair<String?, Boolean>> =
        _errorPasswordMessageBehavior

    private val _errorRepeatedPasswordMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData<Pair<String?, Boolean>>()
    val errorRepeatedPasswordMessageBehavior: LiveData<Pair<String?, Boolean>> =
        _errorRepeatedPasswordMessageBehavior

    private val _registerButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val registerButtonEnabled: LiveData<Boolean> = _registerButtonEnabled

    private val _isPasswordShown: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPasswordShown: LiveData<Boolean> = _isPasswordShown

    private val _isRepeatedPasswordShown: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRepeatedPasswordShown: LiveData<Boolean> = _isRepeatedPasswordShown

    private val _registerState: MutableLiveData<ResourceNetwork<String>> = MutableLiveData()
    val registerState: LiveData<ResourceNetwork<String>> = _registerState

    private val _isErrorMessageVisible: MutableLiveData<Pair<String?,Boolean>> = MutableLiveData()
    val isErrorMessageVisible: LiveData<Pair<String?,Boolean>> = _isErrorMessageVisible


    fun navigateToLoginScreen() {
        router.navigateTo(Screens.loginScreen())
    }

    fun exit() {
        router.exit()
    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            _registerState.postValue(ResourceNetwork.Loading())
             val registerResult = authRepository.register(loginText,surnameText,passwordText)
            if (registerResult is ResourceNetwork.Error) {
                showError(registerResult.message)
            }
            _registerState.postValue(registerResult)
        }
    }

    fun enter() {
        router.newRootScreen(Screens.mainScreen())
    }

    init {
        observe()
    }

    fun onLoginlTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        loginJob?.cancel()
        loginText = s.toString()
        _errorLoginMessageBehavior.postValue(Pair(null, false))
        _registerButtonEnabled.postValue(false)
        loginJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(loginValidation.validate(s.toString()))
            }.collect {
                stateLogin.emit(it)
            }
        }
    }

    fun onSurnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        surnameJob?.cancel()
        surnameText = s.toString()
        _errorSurnameMessageBehavior.postValue(Pair(null, false))
        _registerButtonEnabled.postValue(false)
        surnameJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(surnameValidation.validate(s.toString()))
            }.collect {
                stateSurname.emit(it)
            }
        }
    }

    fun onPasswordlTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        passwordJob?.cancel()
        passwordText = s.toString()
        _errorPasswordMessageBehavior.postValue(Pair(null, false))
        _registerButtonEnabled.postValue(false)
        passwordJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                statePassword.emit(it)
            }
        }
    }

    fun onRepeatedPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        repeatesPasswordJob?.cancel()
        repeatedPasswordText = s.toString()
        _errorRepeatedPasswordMessageBehavior.postValue(Pair(null, false))
        _registerButtonEnabled.postValue(false)
        repeatesPasswordJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                stateRepeatedPassword.emit(it)
            }
        }
    }

    fun observe() {
        viewModelScope.launch {
            combine(
                stateLogin,
                stateSurname,
                statePassword,
                stateRepeatedPassword
            ) { login, surname, password, repeatedPassword ->
                AuthValidationResults(login, surname, password, repeatedPassword)
            }.collect { result ->
                if (result.login is Resource.Success &&
                    result.surname is Resource.Success &&
                    result.password is Resource.Success &&
                    result.repeatedPassword is Resource.Success
                ) {
                    if (!passwordText.equals(repeatedPasswordText)) {
                        _errorRepeatedPasswordMessageBehavior.postValue(
                            Pair(context.getString(R.string.password_must_be_equals), true)
                        )
                    } else {
                        _registerButtonEnabled.postValue(true)
                    }
                } else {
                    result.login?.message?.let {
                        if (it.isNotEmpty()) {
                            _errorLoginMessageBehavior.postValue(Pair(it, true))
                        }
                    }
                    result.surname?.message?.let {
                        if (it.isNotEmpty()) {
                            _errorSurnameMessageBehavior.postValue(Pair(it, true))
                        }
                    }
                    result.password?.message?.let {
                        if (it.isNotEmpty()) {
                            _errorPasswordMessageBehavior.postValue(Pair(it, true))
                        }
                    }
                    result.repeatedPassword?.message?.let {
                        if (it.isNotEmpty()) {
                            _errorRepeatedPasswordMessageBehavior.postValue(Pair(it, true))
                        }
                    }
                    _registerButtonEnabled.postValue(false)
                }
            }
        }
    }

    fun togglePasswordVisibility() {
        _isPasswordShown.value?.let {
            _isPasswordShown.postValue(!it)
        }
    }

    fun toggleRepeatedPasswordVisibility() {
        _isRepeatedPasswordShown.value?.let {
            _isRepeatedPasswordShown.postValue(!it)
        }
    }

    fun showError(message: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            _isErrorMessageVisible.postValue(Pair(message,true))
            delay(3000)
            _isErrorMessageVisible.postValue(Pair(message,false))
        }
    }

    override fun onCleared() {
        super.onCleared()
        loginJob?.cancel()
        surnameJob?.cancel()
        passwordJob?.cancel()
        repeatesPasswordJob?.cancel()
    }

}