package com.example.youngchemist.ui.screen.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.ui.base.workers.UserInfoDonloadingWorker
import com.example.youngchemist.ui.base.workers.UserInfoUploadingWorker
import com.example.youngchemist.ui.screen.Screens
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val userSharedPreferences: UserPreferences,
    private val router: Router
): ViewModel() {

    fun exit() {
        router.exit()
        val userId = "76V1UE5VssV0W8mXenibeUpvQxm1"
        if (!(userId in userSharedPreferences.loggedUsers)) {
            downloadUserInfo()
        }
        workManager.enqueue(PeriodicWorkRequestBuilder<UserInfoUploadingWorker>(4,TimeUnit.HOURS).build())
    }

    fun navigateToScanFragemnt(lastSelectedItemPosition: Int) {
        router.navigateTo(Screens.scanScreen(lastSelectedItemPosition))
    }

    private fun downloadUserInfo() {
        workManager.enqueue(OneTimeWorkRequestBuilder<UserInfoDonloadingWorker>().build())
    }
}