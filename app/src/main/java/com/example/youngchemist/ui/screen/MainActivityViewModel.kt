package com.example.youngchemist.ui.screen

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {

    fun onActivityCreated() {
        router.newRootScreen(Screens.loginScreen())
    }
}