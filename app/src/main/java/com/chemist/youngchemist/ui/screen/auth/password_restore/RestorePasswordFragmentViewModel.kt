package com.chemist.youngchemist.ui.screen.auth.password_restore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.repositories.AuthRepository
import com.chemist.youngchemist.ui.base.validation.ValidationImpl.LoginValidation
import com.chemist.youngchemist.ui.util.Event
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.chemist.youngchemist.ui.util.TextInputResource
import com.chemist.youngchemist.ui.util.toStateFlow
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
class RestorePasswordFragmentViewModel @Inject constructor(
    private val router: Router,
    private val loginValidation: LoginValidation,
    private val authRepository: AuthRepository
) : ViewModel() {

    val login = MutableStateFlow(DEFAULT_LOGIN)
    val errorLoginBehavior =
        MutableStateFlow<TextInputResource<String>>(TextInputResource.ErrorInput(null))

    val enablePasswordChange: StateFlow<Boolean> = combine(errorLoginBehavior) {
        isUserInformationValid()
    }.drop(1).toStateFlow(DEFAULT_ENABLE_REGISTRATION, viewModelScope)

    private val _restorePasswordState: MutableLiveData<Event<ResourceNetwork<String>>> =
        MutableLiveData()
    val restorePasswordState: LiveData<Event<ResourceNetwork<String>>> = _restorePasswordState

    private val _isResultMessageVisible: MutableLiveData<Triple<String?, Boolean, Boolean>> =
        MutableLiveData()
    val isResultMessageVisible: LiveData<Triple<String?, Boolean, Boolean>> =
        _isResultMessageVisible


    init {
        viewModelScope.launch(Dispatchers.Default) {
            login.drop(1).onEach {
                errorLoginBehavior.emit(TextInputResource.InputInProcess())
            }.debounce(300).collect {
                errorLoginBehavior.emit(loginValidation.validate(it))
            }
        }
    }

    private fun isUserInformationValid(): Boolean {
        return errorLoginBehavior.value is TextInputResource.SuccessInput
    }

    fun exit() {
        router.exit()
    }

    fun restorePassword() {
        viewModelScope.launch {
            _restorePasswordState.postValue(Event(ResourceNetwork.Loading()))
            val result = authRepository.restorePassword(login.value)
            _restorePasswordState.postValue(Event(result))
        }
    }

    fun showResultMessage(message: String?, itIsErrorMessage: Boolean = true) {
        viewModelScope.launch(Dispatchers.Default) {
            _isResultMessageVisible.postValue(Triple(message, true, itIsErrorMessage))
            delay(3000)
            _isResultMessageVisible.postValue(Triple(message, false, itIsErrorMessage))
        }
    }

    companion object {
        private const val DEFAULT_LOGIN = ""
        private const val DEFAULT_ENABLE_REGISTRATION = false
    }
}