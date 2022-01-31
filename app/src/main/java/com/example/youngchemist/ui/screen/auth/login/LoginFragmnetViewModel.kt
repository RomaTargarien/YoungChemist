package com.example.youngchemist.ui.screen.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.ui.base.validation.ValidationImpl.LoginValidation
import com.example.youngchemist.ui.base.validation.ValidationImpl.PasswordValidation
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Event
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.UserState
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginFragmnetViewModel @Inject constructor(
    private val router: Router,
    private val loginValidation: LoginValidation,
    private val passwordValidation: PasswordValidation,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {


    private var loginJob: Job? = null
    private var passwordJob: Job? = null

    var loginText: String = ""
    var passwordText: String = ""

    private val stateLogin = MutableStateFlow<Resource<String>>(Resource.Error(""))

    private val statePassword = MutableStateFlow<Resource<String>>(Resource.Error(""))

    private val _enterButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val enterButtonEnabled: LiveData<Boolean> = _enterButtonEnabled

    private val _errorLoginMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData()

    val errorLoginMessageBehavior: LiveData<Pair<String?, Boolean>> = _errorLoginMessageBehavior

    private val _errorPasswordMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData()

    val errorPasswordMessageBehavior: LiveData<Pair<String?, Boolean>> =
        _errorPasswordMessageBehavior

    private val _isPasswordShown: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPasswordShown: LiveData<Boolean> = _isPasswordShown

    private val _loginState: MutableLiveData<Event<ResourceNetwork<String>>> = MutableLiveData()
    val loginState: LiveData<Event<ResourceNetwork<String>>> = _loginState

    private val _isErrorMessageVisible: MutableLiveData<Pair<String?, Boolean>> = MutableLiveData()
    val isErrorMessageVisible: LiveData<Pair<String?, Boolean>> = _isErrorMessageVisible


    init {
        observe()
    }

    fun navigateToRegisterScreen() {
        router.navigateTo(Screens.registerScreen())
    }

    fun navigateToRestorePasswordScreen() {
        router.navigateTo(Screens.restorePasswordScreen())
    }

    fun enter() {
        userPreferences.userState = UserState.LOGIN
        router.newRootScreen(Screens.mainScreen(null))
    }

    fun exit() {
        router.newRootScreen(Screens.authScreen())
    }

    fun login() {
        viewModelScope.launch {
            _loginState.postValue(Event(ResourceNetwork.Loading()))
            val loginResult = authRepository.login(loginText, passwordText)
            _loginState.postValue(Event(loginResult))
        }
    }


    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        loginJob?.cancel()
        loginText = s.toString()
        _errorLoginMessageBehavior.postValue(Pair(null, false))
        _enterButtonEnabled.postValue(false)
        loginJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(loginValidation.validate(s.toString()))
            }.collect {
                stateLogin.emit(it)
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
                statePassword.emit(it)
            }
        }
    }

    private fun observe() {
        viewModelScope.launch {
            combine(stateLogin, statePassword) { login, password ->
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

    fun togglePasswordVisibility() {
        _isPasswordShown.value?.let {
            _isPasswordShown.postValue(!it)
        }
    }

    fun showMessage(message: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            _isErrorMessageVisible.postValue(Pair(message, true))
            delay(3000)
            _isErrorMessageVisible.postValue(Pair(message, false))
        }
    }


    override fun onCleared() {
        super.onCleared()
        loginJob?.cancel()
        passwordJob?.cancel()
    }

}