package com.example.youngchemist.ui.base.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import dagger.assisted.Assisted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class UserInfoUploadingWorker(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val models3D = databaseRepository.getAll3DModels()
                models3D
                    .groupBy { it.userId }
                    .forEach {
                        val result = fireStoreRepository.save3DModels(it.key, it.value)
                        if (result is ResourceNetwork.Error) {
                            throw Exception(result.message)
                        }
                    }
                val userProgress = databaseRepository.getProgress()
                userProgress
                    .groupBy { it.userId }
                    .forEach {
                        val result = fireStoreRepository.saveUserProgress(it.key,it.value)
                        if (result is ResourceNetwork.Error) {
                            throw Exception(result.message)
                        }
                    }
                return@withContext Result.success()
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    Log.d("TAG",it)
                }
                return@withContext Result.retry()
            }
        }
    }
}