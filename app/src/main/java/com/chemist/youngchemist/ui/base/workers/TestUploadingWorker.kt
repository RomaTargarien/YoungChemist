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
class TestUploadingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val userId = inputData.getString(KEY_USER_ID) as String
            val testId = inputData.getString(KEY_TEST_ID) as String
            val testToUpload = databaseRepository.getPassedUserTest(userId, testId)
            testToUpload?.let { test ->
                when (fireStoreRepository.saveTest(userId, test)) {
                    is ResourceNetwork.Error -> {
                        return@withContext Result.retry()
                    }
                    is ResourceNetwork.Success -> {
                        return@withContext Result.success()
                    }
                    else -> {
                    }
                }
            }
            return@withContext Result.failure()
        }
    }

    companion object {
        private const val KEY_USER_ID = "key.user"
        private const val KEY_TEST_ID = "key.test"
    }
}