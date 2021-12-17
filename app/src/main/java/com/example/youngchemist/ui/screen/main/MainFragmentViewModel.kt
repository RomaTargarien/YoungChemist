package com.example.youngchemist.ui.screen.main

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.ui.base.workers.UserInfoDonloadingWorker
import com.example.youngchemist.ui.base.workers.UserInfoUploadingWorker
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Constants.TEST_USER
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.UserState
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val userSharedPreferences: UserPreferences,
    private val router: Router
): ViewModel() {

    init {
        when (userSharedPreferences.userState) {
            UserState.REGISTER -> {
                saveUser(TEST_USER)
                viewModelScope.launch {
                    userSharedPreferences.userStateFlow.emit(Resource.Success())
                }
            }
            UserState.LOGIN -> {
                if (!(TEST_USER in userSharedPreferences.loggedUsers)) {
                    Log.d("TAG","downloaded")
                    downloadUserInfo(TEST_USER)
                } else {
                    viewModelScope.launch {
                        userSharedPreferences.userStateFlow.emit(Resource.Success())
                    }
                }
            }
        }
        //workManager.enqueue(OneTimeWorkRequestBuilder<UserInfoUploadingWorker>().build())
    }

    fun exit() {
        router.exit()
    }

    fun navigateToScanFragemnt(lastSelectedItemPosition: Int) {
        router.navigateTo(Screens.scanScreen(lastSelectedItemPosition))
    }

    private fun downloadUserInfo(userId: String) {
        val data = Data.Builder().putString(KEY_USER_ID,userId).build()
        val workRequest = OneTimeWorkRequestBuilder<UserInfoDonloadingWorker>().setInputData(data).build()
        Log.d("TAG","ID " + workRequest.id.toString())
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
    private fun saveUser(userId: String) {
        val setOfUsers = mutableSetOf<String>()
        setOfUsers.addAll(userSharedPreferences.loggedUsers)
        setOfUsers.add(userId)
        userSharedPreferences.loggedUsers = setOfUsers
    }

    companion object {
        private const val KEY_USER_ID = "key_user"
    }
}