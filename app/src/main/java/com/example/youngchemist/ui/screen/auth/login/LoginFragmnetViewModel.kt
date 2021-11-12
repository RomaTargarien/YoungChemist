package com.example.youngchemist.ui.screen.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.ui.base.validation.ValidationImpl.EmailValidation
import com.example.youngchemist.ui.base.validation.ValidationImpl.PasswordValidation
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Resource
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginFragmnetViewModel @Inject constructor(
    private val router: Router,
    private val emailValidation: EmailValidation,
    private val passwordValidation: PasswordValidation
) : ViewModel() {


    private var loginJob: Job? = null
    private var passwordJob: Job? = null

    var loginText: String = ""
    var passwordText: String = ""

    private val _stateLogin = MutableStateFlow<Resource<String>>(Resource.Error(""))
    val stateLogin = _stateLogin

    private val _statePassword = MutableStateFlow<Resource<String>>(Resource.Error(""))
    val statePassword = _statePassword

    private val _enterButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val enterButtonEnabled: LiveData<Boolean> = _enterButtonEnabled

    private val _errorLoginMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData()
    val errorLoginMessageBehavior: LiveData<Pair<String?, Boolean>> = _errorLoginMessageBehavior

    private val _errorPasswordMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData()
    val errorPasswordMessageBehavior: LiveData<Pair<String?, Boolean>> =
        _errorPasswordMessageBehavior


    init {
        viewModelScope.launch {
            stateLogin.combine(statePassword) { login, password ->
                Pair(login, password)
            }.collect { result ->
                val loginResult = result.first
                val passwordResult = result.second
                if (loginResult is Resource.Success && passwordResult is Resource.Success) {
                    _enterButtonEnabled.postValue(true)
                } else {
                    loginResult.message?.let {
                        if (it.isNotEmpty()) {
                            _errorLoginMessageBehavior.postValue(Pair(it, true))
                        }
                    }
                    passwordResult.message?.let {
                        if (it.isNotEmpty()) {
                            _errorPasswordMessageBehavior.postValue(Pair(it, true))
                        }
                    }
                    _enterButtonEnabled.postValue(false)
                }
            }
        }

    }

    fun navigateToRegisterScreen() {
        router.navigateTo(Screens.registerScreen())
    }

    fun enter() {
        router.newRootScreen(Screens.mainScreen())
    }

    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        loginJob?.cancel()
        loginText = s.toString()
        _errorLoginMessageBehavior.postValue(Pair(null, false))
        _enterButtonEnabled.postValue(false)
        loginJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(emailValidation.validate(s.toString()))
            }.collect {
                _stateLogin.emit(it)
            }
        }
    }

    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        passwordJob?.cancel()
        passwordText = s.toString()
        _errorPasswordMessageBehavior.postValue(Pair(null, false))
        _enterButtonEnabled.postValue(false)
        passwordJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                _statePassword.emit(it)
            }
        }
    }


}