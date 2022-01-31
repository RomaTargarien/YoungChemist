package com.example.youngchemist.ui.base.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.model.UserInfo
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
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
            val result = fireStoreRepository.getUser(userId).await()
            when (result) {
                is ResourceNetwork.Success -> {
                    result.data?.let {
                        val resultTests = saveData(it.passedUserTests)
                        val resultUserProgress = saveData(it.userProgress)
                        val resultModels = saveData(it.saved3DModels)
                        val resultAchievements = saveData(it.doneAchievements)
                        resultTests.await()
                        resultUserProgress.await()
                        resultModels.await()
                        resultAchievements.await()
                    }
                    saveUser(userId)
                    return@withContext Result.success()
                }
                is ResourceNetwork.Error -> {
                    Log.d("TAG", result.message.toString())
                    return@withContext Result.retry()
                }
                else -> {
                }
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

    private fun saveData(data: List<UserInfo>) = CoroutineScope(Dispatchers.Default).async {
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