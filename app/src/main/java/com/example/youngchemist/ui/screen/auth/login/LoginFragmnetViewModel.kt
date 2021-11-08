package com.example.youngchemist.ui.screen.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.screen.main.MainFragment
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginFragmnetViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {

    private var job: Job? = null
    fun navigateToRegisterScreen() {
        router.navigateTo(Screens.registerScreen())
    }

    fun enter(){
        router.newRootScreen(Screens.mainScreen())
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        job?.cancel()
        job = viewModelScope.launch {
            delay(1000)
            Log.d("TAG", s.toString())
        }
    }
}