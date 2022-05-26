package com.chemist.youngchemist.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.db.shared_pref.UserPreferences
import com.chemist.youngchemist.model.Answer
import com.chemist.youngchemist.model.Lecture
import com.chemist.youngchemist.model.Task
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.repositories.FireStoreRepository
import com.chemist.youngchemist.ui.screen.Screens
import com.chemist.youngchemist.ui.util.UserState
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
    private val router: Router
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

