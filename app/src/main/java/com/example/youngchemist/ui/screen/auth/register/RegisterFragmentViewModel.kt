package com.example.youngchemist.ui.screen.auth.register

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterFragmentViewModel @Inject constructor(
    private val router: Router
): ViewModel()  {

    fun navigateToLoginScreen() {
        router.exit()
    }

}