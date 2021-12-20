package com.example.youngchemist.ui.screen.main.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserFragmentViewModel @Inject constructor(
    private val router: Router,
    private val userPreferences: UserPreferences
): ViewModel() {

    fun onBottomSheetStateChanged(value: Float) {
        viewModelScope.launch(Dispatchers.Default) {
            userPreferences.bottomSheetState.emit(value)
        }
    }
}