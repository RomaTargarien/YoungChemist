package com.example.youngchemist.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.model.Answer
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Task
import com.example.youngchemist.model.Test
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.UserState
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@FlowPreview
@HiltViewModel
class AuthFragmentViewModel @Inject constructor(
    private val router: Router,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun navigateToRegisterScreen() {
       router.navigateTo(Screens.registerScreen())
    }

    fun navigateToLoginScreen() {
        router.navigateTo(Screens.loginScreen())
    }

    fun exit() {
        router.exit()
    }
}

