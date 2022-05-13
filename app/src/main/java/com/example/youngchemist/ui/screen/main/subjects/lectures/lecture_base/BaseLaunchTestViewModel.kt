package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture_base

import androidx.lifecycle.ViewModel
import com.example.youngchemist.model.Test
import com.example.youngchemist.ui.screen.Screens
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.FlowPreview

@FlowPreview
abstract class BaseLaunchTestViewModel(
    private val router: Router
): ViewModel() {

    fun navigateToTestScreen(test: Test) {
        router.replaceScreen(Screens.rootTestScreen(test))
    }
}