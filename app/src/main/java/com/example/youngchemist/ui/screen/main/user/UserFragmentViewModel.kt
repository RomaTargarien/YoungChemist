package com.example.youngchemist.ui.screen.main.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.model.User
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.base.validation.ValidationImpl
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.BottomSheetViewModelBase
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.change_email.BottomSheetChangeEmailViewModel
import com.example.youngchemist.ui.screen.main.user.bottom_sheet.change_password.BottomSheetChangePasswordViewModel
import com.example.youngchemist.ui.util.Constants
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private val authRepository: AuthRepository,
    private val fireStoreRepository: FireStoreRepository
) : ViewModel() {

    var bottomSheetViewModel: BottomSheetViewModelBase? = null
    private val _userState: MutableLiveData<ResourceNetwork<User>> = MutableLiveData<ResourceNetwork<User>>()
    val userState: LiveData<ResourceNetwork<User>> = _userState

    init {
        viewModelScope.launch {
            _userState.postValue(ResourceNetwork.Loading())
            val result = fireStoreRepository.getUser(Constants.TEST_USER).await()
            _userState.postValue(result)
        }
    }

    fun changeFamilyName() {

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