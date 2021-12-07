package com.example.youngchemist.ui.screen.main.saved_models

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedModelsFragmentViewModel @Inject constructor(
    private val router: Router
): ViewModel() {
}