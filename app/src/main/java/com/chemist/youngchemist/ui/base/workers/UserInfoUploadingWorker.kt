package com.chemist.youngchemist.ui.base.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.chemist.youngchemist.repositories.FireStoreRepository
import com.chemist.youngchemist.ui.util.ResourceNetwork
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class UserInfoUploadingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val userModels3D = databaseRepository.getAll3DModels()
                val userProgress = databaseRepository.getAllProgress()
                val userDoneAchievements = databaseRepository.getAllAchievements()
                userModels3D
                    .groupBy { it.userId }
                    .forEach {
                        val result = fireStoreRepository.save3DModels(it.key, it.value)
                        if (result is ResourceNetwork.Error) {
                            throw Exception(result.message)
                        }
                    }
                userProgress.groupBy { it.userId }
                    .forEach {
                        val result = fireStoreRepository.saveUserProgress(it.key, it.value)
                        if (result is ResourceNetwork.Error) {
                            throw Exception(result.message)
                        }
                    }
                userDoneAchievements.groupBy { it.userId }
                    .forEach {
                        val result = fireStoreRepository.saveUserDoneAchievements(it.key, it.value)
                        if (result is ResourceNetwork.Error) {
                            throw Exception(result.message)
                        }
                    }
                return@withContext Result.success()
            } catch (e: Exception) {
                return@withContext Result.retry()
            }
        }
    }
}