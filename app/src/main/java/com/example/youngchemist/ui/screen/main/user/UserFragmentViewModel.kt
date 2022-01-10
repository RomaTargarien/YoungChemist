package com.example.youngchemist.ui.screen.main.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.ui.base.validation.ValidationImpl
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserFragmentViewModel @Inject constructor(
    private val router: Router,
    private val userPreferences: UserPreferences,
    private val loginValidation: ValidationImpl.LoginValidation,
    private val passwordValidation: ValidationImpl.PasswordValidation,
    private val surnameValidation: ValidationImpl.SurnameValidation,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val reauthenticateState: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val emailChangeState: MutableStateFlow<Unit?> = MutableStateFlow(null)
    private val passwordChangeState: MutableStateFlow<Unit?> = MutableStateFlow(null)

    private val _reauthenticateResult: MutableLiveData<ResourceNetwork<String>> = MutableLiveData()
    val reauthenticateResult: LiveData<ResourceNetwork<String>> = _reauthenticateResult

    private val _isOldPasswordShown: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOldPasswordShown: LiveData<Boolean> = _isOldPasswordShown

    private val _isNewPasswordShown: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNewPasswordShown: LiveData<Boolean> = _isNewPasswordShown

    val oldPasswordState: MutableStateFlow<String> = MutableStateFlow("")
    val newPasswordState: MutableStateFlow<String> = MutableStateFlow("")
    val emailState: MutableStateFlow<String> = MutableStateFlow("")


    private var oldPasswordJob: Job? = null
    private var newPasswordJob: Job? = null
    private var newEmailJob: Job? = null

    private val _errorMessageNewPasswordBehavior: MutableLiveData<String?> = MutableLiveData()
    val errorMessageNewPasswordBehavior: LiveData<String?> = _errorMessageNewPasswordBehavior

    private val _errorMessageOldPasswordBehavior: MutableLiveData<String?> = MutableLiveData()
    val errorMessageOldPasswordBehavior: LiveData<String?> = _errorMessageOldPasswordBehavior

    private val _errorMessageEmailBehavior: MutableLiveData<String?> = MutableLiveData()
    val errorMessageEmailBehavior: LiveData<String?> = _errorMessageEmailBehavior

    private val _buttonNextState: MutableLiveData<Boolean> = MutableLiveData(false)
    val buttonNextState: LiveData<Boolean> = _buttonNextState

    private val _buttonChangeState: MutableLiveData<Boolean> = MutableLiveData(false)
    val buttonChangeState: LiveData<Boolean> = _buttonChangeState

    init {
        observeEmailChangeState()
        observePasswordChangeState()
    }

    fun onBottomSheetStateChanged(value: Float) {
        viewModelScope.launch(Dispatchers.Default) {
            userPreferences.bottomSheetState.emit(value)
        }
    }

    fun reauthenticate() {
        viewModelScope.launch {
            _reauthenticateResult.postValue(ResourceNetwork.Loading())
            val result = authRepository.reauthenticate(oldPasswordState.value)
            _reauthenticateResult.postValue(result)
        }
    }

    fun changePassword() {
        viewModelScope.launch {
            passwordChangeState.emit(Unit)
        }
    }

    fun changeEmail() {
        viewModelScope.launch {
            emailChangeState.emit(Unit)
        }
    }

    fun observePasswordChangeState() {
        viewModelScope.launch {

        }
    }

    fun observeEmailChangeState() {
        viewModelScope.launch {
            emailChangeState.filterNotNull().combine(emailState) { _, email ->
                email
            }.collect {
                val result = authRepository.updateEmail(it)
            }
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
        oldPasswordJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                if (it is Resource.Success) {
                    _buttonNextState.postValue(true)
                    oldPasswordState.emit(s.toString())
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
        newPasswordJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(passwordValidation.validate(s.toString()))
            }.collect {
                if (it is Resource.Success) {
                    _buttonChangeState.postValue(true)
                    newPasswordState.emit(s.toString())
                } else {
                    _errorMessageNewPasswordBehavior.postValue(it.message)
                }
            }
        }
    }
}