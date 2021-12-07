package com.example.youngchemist.ui.screen.main.qr.qr_code

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.youngchemist.ui.screen.Screens
import com.github.terrakok.cicerone.ResultListener
import com.github.terrakok.cicerone.ResultListenerHandler
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrCodeFragmentViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {

    fun exit(lastSelectedItemPosition: Int) {
        router.newRootScreen(Screens.mainScreen(lastSelectedItemPosition))
    }
}