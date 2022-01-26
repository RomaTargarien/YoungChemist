package com.example.youngchemist.ui.screen.auth.register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.R
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.model.AuthResults
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.ui.base.validation.ValidationImpl.*
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Event
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.UserState
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
class RegisterFragmentViewModel @Inject constructor(
    private val router: Router,
    private val loginValidation: LoginValidation,
    private val nameValidation: NameValidation,
    private val passwordValidation: PasswordValidation,
    private val surnameValidation: SurnameValidation,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val authResults: AuthResults

    private var loginJob: Job? = null
    private var nameJob: Job? = null
    private var surnameJob: Job? = null
    private var passwordJob: Job? = null
    private var repeatesPasswordJob: Job? = null

    private val stateLogin = MutableStateFlow<Resource<String>>(Resource.Error(""))
    private val stateName = MutableStateFlow<Resource<String>>(Resource.Error(""))
    private val stateSurname = MutableStateFlow<Resource<String>>(Resource.Error(""))
    private val statePassword = MutableStateFlow<Resource<String>>(Resource.Error(""))
    private val stateRepeatedPassword = MutableStateFlow<Resource<String>>(Resource.Error(""))

    private val _errorLoginMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData<Pair<String?, Boolean>>()
    val errorLoginMessageBehavior: LiveData<Pair<String?, Boolean>> = _errorLoginMessageBehavior

    private val _errorNameMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData<Pair<String?, Boolean>>()
    val errorNameMessageBehavior: LiveData<Pair<String?, Boolean>> = _errorNameMessageBehavior

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

    private val _registerState: MutableLiveData<Event<ResourceNetwork<String>>> = MutableLiveData()
    val registerState: LiveData<Event<ResourceNetwork<String>>> = _registerState

    private val _isErrorMessageVisible: MutableLiveData<Pair<String?, Boolean>> = MutableLiveData()
    val isErrorMessageVisible: LiveData<Pair<String?, Boolean>> = _isErrorMessageVisible


    fun navigateToLoginScreen() {
        router.navigateTo(Screens.loginScreen())
    }

    fun exit() {
        router.exit()
    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            _registerState.postValue(Event(ResourceNetwork.Loading()))
            val registerResult = authRepository.register(authResults)
            _registerState.postValue(Event(registerResult))
        }
    }

    fun enter() {
        userPreferences.userState = UserState.REGISTER
        router.newRootScreen(Screens.mainScreen(null))
    }

    init {
        authResults = AuthResults()
        observe()
    }


    fun onLoginlTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        loginJob?.cancel()
        authResults.login = s.toString()
        _errorLoginMessageBehavior.postValue(Pair(null, false))
        _registerButtonEnabled.postValue(false)
        loginJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(loginValidation.validate(s.toString()))
            }.collect {
                if (it is Resource.Error) {
                    _errorLoginMessageBehavior.postValue(Pair(it.message, true))
                }
                stateLogin.emit(it)
            }
        }
    }

    fun onNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        nameJob?.cancel()
        authResults.name = s.toString()
        _errorNameMessageBehavior.postValue(Pair(null, false))
        _registerButtonEnabled.postValue(false)
        nameJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(nameValidation.validate(s.toString()))
            }.collect {
                if (it is Resource.Error) {
                    _errorNameMessageBehavior.postValue(Pair(it.message, true))
                }
                stateName.emit(it)
            }
        }
    }

    fun onSurnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        surnameJob?.cancel()
        authResults.surname = s.toString()
        _errorSurnameMessageBehavior.postValue(Pair(null, false))
        _registerButtonEnabled.postValue(false)
        surnameJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(surnameValidation.validate(s.toString()))
            }.collect {
                if (it is Resource.Error) {
                    _errorSurnameMessageBehavior.postValue(Pair(it.message, true))
                }
                stateSurname.emit(it)
            }
        }
    }

    fun onPasswordlTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        passwordJob?.cancel()
        authResults.password = s.toString()
        _errorPasswordMessageBehavior.postValue(Pair(null, false))
        _registerButtonEnabled.postValue(false)
        passwordJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                if (it is Resource.Error) {
                    _errorPasswordMessageBehavior.postValue(Pair(it.message, true))
                }
                statePassword.emit(it)
            }
        }
    }

    fun onRepeatedPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        repeatesPasswordJob?.cancel()
        authResults.repeatedPassword = s.toString()
        _errorRepeatedPasswordMessageBehavior.postValue(Pair(null, false))
        _registerButtonEnabled.postValue(false)
        repeatesPasswordJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                if (it is Resource.Error) {
                    _errorRepeatedPasswordMessageBehavior.postValue(Pair(it.message, true))
                }
                stateRepeatedPassword.emit(it)
            }
        }
    }

    fun observe() {
        viewModelScope.launch {
            combine(
                stateLogin,
                stateName,
                stateSurname,
                statePassword,
                stateRepeatedPassword
            ) { login, name, surname, password, repeatedPassword ->
                AuthResults(
                    loginValidation = login,
                    nameValidation = name,
                    surnameValidation = surname,
                    passwordValidation = password,
                    repeatedPasswordValidation = repeatedPassword
                )
            }.collect { result ->
                if (result.allSuccess()) {
                    if (!authResults.arePasswordEquals()) {
                        _errorRepeatedPasswordMessageBehavior.postValue(
                            Pair(context.getString(R.string.password_must_be_equals), true)
                        )
                    } else {
                        _registerButtonEnabled.postValue(true)
                    }
                } else {
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
            _isErrorMessageVisible.postValue(Pair(message, true))
            delay(3000)
            _isErrorMessageVisible.postValue(Pair(message, false))
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