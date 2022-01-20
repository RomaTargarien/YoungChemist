package com.example.youngchemist.ui.screen.main.achievements

import androidx.lifecycle.ViewModel
import com.example.youngchemist.repositories.FireStoreRepository
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AchievementsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository
):ViewModel() {

    init {

    }
}