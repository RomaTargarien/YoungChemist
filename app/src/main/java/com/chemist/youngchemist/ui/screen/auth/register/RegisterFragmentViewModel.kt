package com.chemist.youngchemist.ui.screen.auth.register

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.R
import com.chemist.youngchemist.db.shared_pref.UserPreferences
import com.chemist.youngchemist.repositories.AuthRepository
import com.chemist.youngchemist.ui.base.validation.ValidationImpl.*
import com.chemist.youngchemist.ui.screen.Screens
import com.chemist.youngchemist.ui.util.*
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class RegisterFragmentViewModel @Inject constructor(
    private val router: Router,
    private val loginValidation: LoginValidation,
    private val nameValidation: NameValidation,
    private val passwordValidation: PasswordValidation,
    private val surnameValidation: SurnameValidation,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
    private val resources: Resources
) : ViewModel() {

    val name = MutableStateFlow(DEFAULT_NAME)
    val errorNameBehavior =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    val surname = MutableStateFlow(DEFAULT_SURNAME)
    val errorSurnameBehavior =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    val login = MutableStateFlow(DEFAULT_LOGIN)
    val errorLoginBehavior =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    val password = MutableStateFlow(DEFAULT_PASSWORD)
    val errorPasswordBehavior =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    val repeatedPassword = MutableStateFlow(DEFAULT_REPEATED_PASSWORD)
    val errorRepeatedPasswordBehavior =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    val enableRegistration: StateFlow<Boolean> = combine(
        errorNameBehavior,
        errorSurnameBehavior,
        errorPasswordBehavior,
        errorRepeatedPasswordBehavior,
        errorLoginBehavior
    ) { _ -> isUserInformationValid() }
        .drop(1)
        .toStateFlow(DEFAULT_ENABLE_REGISTRATION, viewModelScope)

    val isPasswordShown = MutableStateFlow(DEFAULT_PASSWORD_VISIBILITY)
    val isRepeatedPasswordShown = MutableStateFlow(DEFAULT_REPEATED_PASSWORD_VISIBILITY)

    private val _registerState: MutableLiveData<Event<ResourceNetwork<String>>> = MutableLiveData()
    val registerState: LiveData<Event<ResourceNetwork<String>>> = _registerState

    private val _isErrorMessageVisible: MutableLiveData<Pair<String?, Boolean>> = MutableLiveData()
    val isErrorMessageVisible: LiveData<Pair<String?, Boolean>> = _isErrorMessageVisible

    init {
        viewModelScope.launch(Dispatchers.Default) {
            name.drop(1).onEach {
                errorNameBehavior.emit(TextInputResource.InputInProcess())
            }.debounce(300).collect {
                errorNameBehavior.emit(nameValidation.validate(it))
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            surname.drop(1).onEach {
                errorSurnameBehavior.emit(TextInputResource.InputInProcess())
            }.debounce(300).collect {
                errorSurnameBehavior.emit(surnameValidation.validate(it))
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            login.drop(1).onEach {
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

        viewModelScope.launch(Dispatchers.Default) {
            repeatedPassword.drop(1).onEach {
                errorRepeatedPasswordBehavior.emit(TextInputResource.InputInProcess())
            }.debounce(300).collect {
                errorRepeatedPasswordBehavior.emit(passwordValidation.validate(it))
            }
        }
    }

    private fun isUserInformationValid(): Boolean {
        if (errorPasswordBehavior.value is TextInputResource.SuccessInput
            && errorRepeatedPasswordBehavior.value is TextInputResource.SuccessInput
        ) {
            if (password.value != repeatedPassword.value) {
                viewModelScope.launch {
                    errorRepeatedPasswordBehavior.emit(
                        TextInputResource.ErrorInput(
                            resources.getString(R.string.password_must_be_equals)
                        )
                    )
                }
                return false
            }
        }
        return errorRepeatedPasswordBehavior.value is TextInputResource.SuccessInput
                && errorNameBehavior.value is TextInputResource.SuccessInput
                && errorSurnameBehavior.value is TextInputResource.SuccessInput
                && errorLoginBehavior.value is TextInputResource.SuccessInput
                && errorPasswordBehavior.value is TextInputResource.SuccessInput
    }

    fun navigateToLoginScreen() {
        router.navigateTo(Screens.loginScreen())
    }

    fun exit() {
        router.exit()
    }

    fun register() {
        viewModelScope.launch(Dispatchers.IO) {
            _registerState.postValue(Event(ResourceNetwork.Loading()))
            val registerResult = authRepository.register(
                login.value,
                password.value,
                name.value,
                surname.value)
            _registerState.postValue(Event(registerResult))
        }
    }

    fun enter() {
        userPreferences.userState = UserState.REGISTER
        router.newRootScreen(Screens.mainScreen(null))
    }

    fun togglePasswordVisibility() {
        viewModelScope.launch { isPasswordShown.emit(!isPasswordShown.value) }
    }

    fun toggleRepeatedPasswordVisibility() {
        viewModelScope.launch { isRepeatedPasswordShown.emit(!isRepeatedPasswordShown.value) }
    }

    fun showError(message: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            _isErrorMessageVisible.postValue(Pair(message, true))
            delay(3000)
            _isErrorMessageVisible.postValue(Pair(message, false))
        }
    }

    companion object {
        private const val DEFAULT_LOGIN = ""
        private const val DEFAULT_PASSWORD = ""
        private const val DEFAULT_NAME = ""
        private const val DEFAULT_SURNAME = ""
        private const val DEFAULT_REPEATED_PASSWORD = ""
        private const val DEFAULT_ENABLE_REGISTRATION = false
        private const val DEFAULT_PASSWORD_VISIBILITY = false
        private const val DEFAULT_REPEATED_PASSWORD_VISIBILITY = false
    }
}