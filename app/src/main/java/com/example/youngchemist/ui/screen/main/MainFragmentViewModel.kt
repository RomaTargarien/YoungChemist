package com.example.youngchemist.ui.screen.main

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val router: Router
): ViewModel() {

    fun exit() {
        router.exit()
    }
}