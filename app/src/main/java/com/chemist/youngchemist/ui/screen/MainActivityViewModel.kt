package com.chemist.youngchemist.ui.screen

import androidx.lifecycle.ViewModel
import com.chemist.youngchemist.db.shared_pref.UserPreferences
import com.chemist.youngchemist.ui.util.UserState
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val router: Router,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun onActivityCreated() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            userPreferences.userState = UserState.LOGGED
            router.newRootScreen(Screens.mainScreen(null))
        } else {
            router.newRootScreen(Screens.authScreen())
        }
    }
}