package com.chemist.youngchemist.ui.base.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chemist.youngchemist.db.shared_pref.UserPreferences
import com.chemist.youngchemist.model.UserInfo
import com.chemist.youngchemist.model.user.Model3D
import com.chemist.youngchemist.model.user.PassedUserTest
import com.chemist.youngchemist.model.user.UserAchievement
import com.chemist.youngchemist.model.user.UserProgress
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.chemist.youngchemist.repositories.FireStoreRepository
import com.chemist.youngchemist.ui.util.ResourceNetwork
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*

@HiltWorker
class UserInfoDownloadingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository,
    private val userPreferences: UserPreferences
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val userId = inputData.getString(KEY_USER_ID) as String
            when (val result = fireStoreRepository.getUser(userId).await()) {
                is ResourceNetwork.Success -> {
                    result.data?.let {
                        val resultTests = saveDataAsync(it.passedUserTests)
                        val resultUserProgress = saveDataAsync(it.userProgress)
                        val resultModels = saveDataAsync(it.saved3DModels)
                        val resultAchievements = saveDataAsync(it.doneAchievements)
                        resultTests.await()
                        resultUserProgress.await()
                        resultModels.await()
                        resultAchievements.await()
                    }
                    saveUser(userId)
                    return@withContext Result.success()
                }
                is ResourceNetwork.Error -> {
                    return@withContext Result.retry()
                }
                else -> {}
            }
            return@withContext Result.failure()
        }
    }

    private fun saveUser(userId: String) {
        val setOfUsers = mutableSetOf<String>()
        setOfUsers.addAll(userPreferences.loggedUsers)
        setOfUsers.add(userId)
        userPreferences.loggedUsers = setOfUsers
    }

    private fun saveDataAsync(data: List<UserInfo>) = CoroutineScope(Dispatchers.Default).async {
        if (data.isNotEmpty()) {
            when (data[0]) {
                is PassedUserTest -> {
                    for (item in data) {
                        databaseRepository.savePassedUserTest(item as PassedUserTest)
                    }
                }
                is Model3D -> {
                    for (item in data) {
                        databaseRepository.save3DModel(item as Model3D)
                    }
                }
                is UserProgress -> {
                    for (item in data) {
                        databaseRepository.saveProgress(item as UserProgress)
                    }
                }
                is UserAchievement -> {
                    for (item in data) {
                        databaseRepository.saveAchievement(item as UserAchievement)
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_USER_ID = "key_user"
    }
}