package com.example.youngchemist.ui.screen.main.subjects

import androidx.lifecycle.ViewModel
import com.example.youngchemist.ui.screen.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubjectsFragmentViewModel @Inject constructor(
    private val router: Router
): ViewModel() {

    fun navigateToLecturesListScreen(title: String) {
        router.navigateTo(Screens.lecturesListScreen(title))
    }
}