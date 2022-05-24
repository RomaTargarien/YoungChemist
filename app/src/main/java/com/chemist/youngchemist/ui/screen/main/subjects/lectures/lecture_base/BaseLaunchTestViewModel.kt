package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture_base

import androidx.lifecycle.ViewModel
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.ui.screen.Screens
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.FlowPreview

@FlowPreview
abstract class BaseLaunchTestViewModel(
    private val router: Router
): ViewModel() {

    fun navigateToTestScreen(test: Test) {
        router.navigateTo(Screens.rootTestScreen(test))
    }
}