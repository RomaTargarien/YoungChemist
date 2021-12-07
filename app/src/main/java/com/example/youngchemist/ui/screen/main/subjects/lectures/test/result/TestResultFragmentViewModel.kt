package com.example.youngchemist.ui.screen.main.subjects.lectures.test.result

import androidx.lifecycle.ViewModel
import com.example.youngchemist.ui.screen.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestResultFragmentViewModel @Inject constructor(
    private val router: Router
): ViewModel(){

    fun exit() {
        router.exit()
    }

    fun goToMainScreen() {
        router.newRootScreen(Screens.mainScreen(0))
    }
}