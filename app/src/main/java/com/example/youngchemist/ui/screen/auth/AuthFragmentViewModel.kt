package com.example.youngchemist.ui.screen.auth

import androidx.lifecycle.ViewModel
import com.example.youngchemist.ui.screen.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthFragmentViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {

    fun onAuthFragmnetCreated() {
        router.navigateTo(Screens.loginScreen())
    }
}