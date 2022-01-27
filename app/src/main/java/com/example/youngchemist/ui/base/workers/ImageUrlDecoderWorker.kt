package com.example.youngchemist.ui.base.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.ui.util.BitmapUtils
import com.example.youngchemist.ui.util.Constants.ACHIEVEMENT_DATABASE
import com.example.youngchemist.ui.util.Constants.SUBJECT_DATABASE
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@HiltWorker
class ImageUrlDecoderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val databaseRepository: DatabaseRepository
): CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val databaseType = inputData.getInt(KEY_DATABASE_TYPE, -1)
            val primaryKey = inputData.getInt(KEY_PRIMARY_KEY, -1)
            when (databaseType) {
                SUBJECT_DATABASE -> {
                    val subject = databaseRepository.getSubjectByPrimaryKey(primaryKey)
                    val result = getBitmapFromURL(subject.icon_url)
                    when (result) {
                        is ResourceNetwork.Success -> {
                            result.data?.let {
                                subject.iconByteArray = BitmapUtils.convertBitmapToByteArray(it)
                                databaseRepository.saveSubject(subject)
                                return@withContext Result.success()
                            }
                        }
                        is ResourceNetwork.Error -> {
                            Log.d("TAG", result.message.toString())
                            return@withContext Result.retry()
                        }
                    }
                }
                ACHIEVEMENT_DATABASE -> {
                    val achievement = databaseRepository.getAchievementByPrimaryKey(primaryKey)
                    Log.d("TAG",achievement.toString())
                    val result = getBitmapFromURL(achievement.imageUrl)
                    when (result) {
                        is ResourceNetwork.Success -> {
                            result.data?.let {
                                achievement.iconByteArray = BitmapUtils.convertBitmapToByteArray(it)
                                databaseRepository.saveAchievement(achievement)
                                return@withContext Result.success()
                            }
                        }
                        is ResourceNetwork.Error -> {
                            Log.d("TAG", result.message.toString())
                            return@withContext Result.retry()
                        }
                    }
                }
                else -> {
                    return@withContext Result.failure()
                }
            }
            return@withContext Result.failure()
        }
    }

    private suspend fun getBitmapFromURL(src: String?) = withContext(Dispatchers.IO) {
        safeCall {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            ResourceNetwork.Success(BitmapFactory.decodeStream(input))
        }
    }

    companion object {
        private const val KEY_PRIMARY_KEY = "key.achievement.primary.key"
        private const val KEY_DATABASE_TYPE = "key.database.type"
    }
}