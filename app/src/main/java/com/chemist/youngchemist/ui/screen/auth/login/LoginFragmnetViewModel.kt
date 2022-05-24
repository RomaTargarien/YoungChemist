package com.chemist.youngchemist.ui.screen.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.db.shared_pref.UserPreferences
import com.chemist.youngchemist.repositories.AuthRepository
import com.chemist.youngchemist.ui.base.validation.ValidationImpl.LoginValidation
import com.chemist.youngchemist.ui.base.validation.ValidationImpl.PasswordValidation
import com.chemist.youngchemist.ui.screen.Screens
import com.chemist.youngchemist.ui.util.*
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class LoginFragmnetViewModel @Inject constructor(
    private val router: Router,
    private val loginValidation: LoginValidation,
    private val passwordValidation: PasswordValidation,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val login = MutableStateFlow(userPreferences.lastEmail ?: DEFAULT_LOGIN)
    val errorLoginBehavior =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    val password = MutableStateFlow(DEFAULT_PASSWORD)
    val errorPasswordBehavior =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    val enableRegistration: StateFlow<Boolean> =
        combine(errorPasswordBehavior, errorLoginBehavior) { _ ->
            isUserInformationValid()
        }.drop(1).toStateFlow(DEFAULT_ENABLE_REGISTRATION, viewModelScope)

    val isPasswordShown = MutableStateFlow(DEFAULT_PASSWORD_VISIBILITY)

    private val _loginState: MutableLiveData<Event<ResourceNetwork<String>>> = MutableLiveData()
    val loginState: LiveData<Event<ResourceNetwork<String>>> = _loginState

    private val _isErrorMessageVisible: MutableLiveData<Pair<String?, Boolean>> = MutableLiveData()
    val isErrorMessageVisible: LiveData<Pair<String?, Boolean>> = _isErrorMessageVisible

    init {
        viewModelScope.launch(Dispatchers.Default) {
            login.drop(if (userPreferences.lastEmail == null) 1 else 0).onEach {
                errorLoginBehavior.emit(TextInputResource.InputInProcess())
            }.debounce(300).collect {
                errorLoginBehavior.emit(loginValidation.validate(it))
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            password.drop(1).onEach {
                errorPasswordBehavior.emit(TextInputResource.InputInProcess())
            }.debounce(300).collect {
                errorPasswordBehavior.emit(passwordValidation.validate(it))
            }
        }
    }

    private fun isUserInformationValid(): Boolean {
        return errorLoginBehavior.value is TextInputResource.SuccessInput
                && errorPasswordBehavior.value is TextInputResource.SuccessInput
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
            val loginResult = authRepository.login(login.value, password.value)
            if (loginResult is ResourceNetwork.Success) {
                userPreferences.lastEmail = login.value
            }
            _loginState.postValue(Event(loginResult))
        }
    }

    fun togglePasswordVisibility() {
        viewModelScope.launch {
            isPasswordShown.emit(!isPasswordShown.value)
        }
    }

    fun showMessage(message: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            _isErrorMessageVisible.postValue(Pair(message, true))
            delay(3000)
            _isErrorMessageVisible.postValue(Pair(message, false))
        }
    }

    companion object {
        private const val DEFAULT_LOGIN = ""
        private const val DEFAULT_PASSWORD = ""
        private const val DEFAULT_PASSWORD_VISIBILITY = false
        private const val DEFAULT_ENABLE_REGISTRATION = false
    }
}