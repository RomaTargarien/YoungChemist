package com.example.youngchemist.ui.screen.main.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.model.User
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.base.validation.ValidationImpl
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.BottomSheetViewModelBase
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.change_email.BottomSheetChangeEmailViewModel
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.change_password.BottomSheetChangePasswordViewModel
import com.example.youngchemist.ui.util.Constants
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@HiltViewModel
class UserFragmentViewModel @Inject constructor(
    private val router: Router,
    private val userPreferences: UserPreferences,
    private val loginValidation: ValidationImpl.LoginValidation,
    private val passwordValidation: ValidationImpl.PasswordValidation,
    private val surnameValidation: ValidationImpl.SurnameValidation,
    private val nameValidation: ValidationImpl.NameValidation,
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FireStoreRepository
) : ViewModel() {

    var bottomSheetViewModel: BottomSheetViewModelBase? = null

    private val _userState: MutableLiveData<ResourceNetwork<User>> = MutableLiveData<ResourceNetwork<User>>()
    val userState: LiveData<ResourceNetwork<User>> = _userState

    private val _currentUserFlow: MutableStateFlow<User?> = MutableStateFlow(null)
    val currentUserFlow: StateFlow<User?> = _currentUserFlow

    private val newSurnameFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val newNameFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private var surnameJob: Job? = null
    private var nameJob: Job? = null

    private val _errorMessageNewSurnameBehavior: MutableLiveData<String?> = MutableLiveData(null)
    val errorMessageNewSurnameBehavior: LiveData<String?> = _errorMessageNewSurnameBehavior
    private val _buttonChangeSurnameState: MutableLiveData<Boolean> = MutableLiveData()
    val buttonChangeSurnameState: LiveData<Boolean> = _buttonChangeSurnameState

    private val _errorMessageNewNameBehavior: MutableLiveData<String?> = MutableLiveData(null)
    val errorMessageNewNameBehavior: LiveData<String?> = _errorMessageNewNameBehavior
    private val _buttonChangeNameState: MutableLiveData<Boolean> = MutableLiveData()
    val buttonChangeNameState: LiveData<Boolean> = _buttonChangeNameState

    fun getUserInfo() {
        viewModelScope.launch {
            _userState.postValue(ResourceNetwork.Loading())
            val result = fireStoreRepository.getUser(Constants.TEST_USER).await()
            if (result is ResourceNetwork.Success) {
                result.data?.let {
                    _currentUserFlow.emit(it)
                }
            }
            _userState.postValue(result)
        }
    }

    fun onSurnameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        surnameJob?.cancel()
        _errorMessageNewSurnameBehavior.postValue(null)
        _buttonChangeSurnameState.postValue(false)
        surnameJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(300)
                emit(Pair(s.toString(),surnameValidation.validate(s.toString())))
            }.combine(currentUserFlow) { surnamePair,currentUser ->
                Pair(surnamePair,currentUser?.surname)
            }.collect {
                val surnamePair = it.first
                val currentUserSurname = it.second
                if (surnamePair.second is Resource.Success) {
                    if (surnamePair.first == currentUserSurname) {
                        _buttonChangeSurnameState.postValue(false)
                    } else {
                        newSurnameFlow.emit(surnamePair.first)
                        _buttonChangeSurnameState.postValue(true)
                    }
                }
                if (surnamePair.second is Resource.Error) {
                    _errorMessageNewSurnameBehavior.postValue(surnamePair.second.message)
                }
            }
        }
    }

    fun onNameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        nameJob?.cancel()
        _errorMessageNewNameBehavior.postValue(null)
        _buttonChangeNameState.postValue(false)
        nameJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(300)
                emit(Pair(s.toString(),nameValidation.validate(s.toString())))
            }.combine(currentUserFlow) { namePair,currentUser ->
                Pair(namePair,currentUser?.name)
            }.collect {
                val namePair = it.first
                val currentUserName = it.second
                if (namePair.second is Resource.Success) {
                    if (namePair.first == currentUserName) {
                        _buttonChangeNameState.postValue(false)
                    } else {
                        newNameFlow.emit(namePair.first)
                        _buttonChangeNameState.postValue(true)
                    }
                }
                if (namePair.second is Resource.Error) {
                    _errorMessageNewNameBehavior.postValue(namePair.second.message)
                }
            }
        }
    }

    fun changeUserName() {
        _currentUserFlow.value?.let { user ->
            user.name = newNameFlow.value!!
        }
        viewModelScope.launch {
            currentUserFlow.value?.let { user ->
                fireStoreRepository.updateUserInfo(user)
            }
        }
    }

    fun changeUserSurname() {
        _currentUserFlow.value?.let { user ->
            user.surname = newSurnameFlow.value!!
        }
        viewModelScope.launch {
            currentUserFlow.value?.let { user ->
                fireStoreRepository.updateUserInfo(user)
            }
        }
    }

    fun onBottomSheetStateChanged(value: Float) {
        viewModelScope.launch(Dispatchers.Default) {
            userPreferences.bottomSheetState.emit(value)
        }
    }

    fun createChangePasswordViewModel(): BottomSheetViewModelBase {
        bottomSheetViewModel = BottomSheetChangePasswordViewModel(passwordValidation,authRepository)
        return bottomSheetViewModel!!
    }
    fun createChangeEmailViewModel() : BottomSheetViewModelBase {
        bottomSheetViewModel = BottomSheetChangeEmailViewModel(loginValidation,passwordValidation,authRepository)
        return bottomSheetViewModel!!
    }

    fun destroyBottomSheetViewModel() {
        bottomSheetViewModel = null
    }
}