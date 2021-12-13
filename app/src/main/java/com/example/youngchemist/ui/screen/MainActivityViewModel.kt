package com.example.youngchemist.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val router: Router,
    private val userPreferences: UserPreferences,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    fun onActivityCreated() {
        router.newRootScreen(Screens.authScreen())
    }

    init {
        viewModelScope.launch {
            val userId = "76V1UE5VssV0W8mXenibeUpvQxm1"
            if (userId in userPreferences.loggedUsers) {
                Log.d("TAG","old user")
            } else {
                val result = fireStoreRepository.getUser(userId)
                val user = result.await()
                if (user is ResourceNetwork.Success) {
                    user.data?.let {
                        for (item in it.passedUserTests) {
                            databaseRepository.savePassedUserTest(item)
                        }
                        for (item in it.saved3DModels) {
                            databaseRepository.save3DModel(item)
                        }
                        for (item in it.userProgress) {
                            databaseRepository.saveProgress(item)
                        }
                    }
                }
                val setOfUsers = mutableSetOf<String>()
                setOfUsers.addAll(userPreferences.loggedUsers)
                setOfUsers.add(userId)
                userPreferences.loggedUsers = setOfUsers
            }
        }

    }
}