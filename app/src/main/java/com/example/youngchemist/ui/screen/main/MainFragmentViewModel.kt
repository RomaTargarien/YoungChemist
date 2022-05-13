package com.example.youngchemist.ui.screen.main

import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.ui.base.workers.UserInfoDownloadingWorker
import com.example.youngchemist.ui.base.workers.UserInfoUploadingWorker
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.UserState
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val userSharedPreferences: UserPreferences,
    private val router: Router,
    private val authRepository: AuthRepository,
    private val currentUser: FirebaseUser
) : ViewModel() {

    private val _bottomSheetState: MutableLiveData<Float> = MutableLiveData()
    val bottomSheetState: LiveData<Float> = _bottomSheetState

    init {
        viewModelScope.launch {
            userSharedPreferences.bottomSheetState.emit(-1f)
        }
        when (userSharedPreferences.userState) {
            UserState.REGISTER -> {
                saveUser(currentUser.uid)
                viewModelScope.launch {
                    userSharedPreferences.userStateFlow.emit(Resource.Success())
                }
            }
            UserState.LOGIN -> {
                if (!(currentUser.uid in userSharedPreferences.loggedUsers)) {
                    downloadUserInfo(currentUser.uid)
                } else {
                    viewModelScope.launch {
                        userSharedPreferences.userStateFlow.emit(Resource.Success())
                    }
                }
            }
            UserState.LOGGED -> {
                viewModelScope.launch {
                    userSharedPreferences.userStateFlow.emit(Resource.Success())
                }
            }
        }

        viewModelScope.launch {
            userSharedPreferences.bottomSheetState.filterNotNull().collect {
                _bottomSheetState.postValue(it)
            }
        }
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<UserInfoUploadingWorker>(4, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(work)
    }

    fun exit() {
        router.exit()
    }

    fun navigateToScanFragemnt(lastSelectedItemPosition: Int) {
        router.navigateTo(Screens.scanScreen(lastSelectedItemPosition))
    }

    private fun downloadUserInfo(userId: String) {
        val data = Data.Builder().putString(KEY_USER_ID, userId).build()
        val workRequest =
            OneTimeWorkRequestBuilder<UserInfoDownloadingWorker>().setInputData(data).build()
        workManager.enqueue(workRequest)
        viewModelScope.launch {
            workManager.getWorkInfoByIdLiveData(workRequest.id).asFlow().collect {
                when (it.state.name) {
                    "RUNNING" -> {
                        userSharedPreferences.userStateFlow.emit(Resource.Loading())
                    }
                    "SUCCEEDED" -> {
                        userSharedPreferences.userStateFlow.emit(Resource.Success())
                    }
                    "FAILED" -> {
                        userSharedPreferences.userStateFlow.emit(Resource.Error("error"))
                    }
                }
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            val result = authRepository.logOut()
            if (result is ResourceNetwork.Success) {
                router.newRootScreen(Screens.authScreen())
            }
        }
    }

    private fun saveUser(userId: String) {
        val setOfUsers = mutableSetOf<String>()
        setOfUsers.addAll(userSharedPreferences.loggedUsers)
        setOfUsers.add(userId)
        userSharedPreferences.loggedUsers = setOfUsers
    }

    override fun onCleared() {
        super.onCleared()
    }

    companion object {
        private const val KEY_USER_ID = "key_user"
    }
}