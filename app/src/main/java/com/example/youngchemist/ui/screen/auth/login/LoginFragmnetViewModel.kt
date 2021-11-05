package com.example.youngchemist.ui.screen.auth.login

import androidx.lifecycle.ViewModel
import com.example.youngchemist.ui.screen.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginFragmnetViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {

    fun navigateToRegisterScreen() {
        router.navigateTo(Screens.registerScreen())
    }
}