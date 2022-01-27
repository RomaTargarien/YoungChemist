package com.example.youngchemist.ui.screen.main.achievements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.ui.base.workers.ImageUrlDecoderWorker
import com.example.youngchemist.ui.util.Constants.ACHIEVEMENT_DATABASE
import com.example.youngchemist.ui.util.Constants.TEST_USER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsFragmentViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _doneTestsCount: MutableLiveData<Int> = MutableLiveData()
    val doneTestsCount: LiveData<Int> = _doneTestsCount

    private val _readenLecturesCount: MutableLiveData<Int> = MutableLiveData()
    val readenLecturesCount: LiveData<Int> = _readenLecturesCount

    private val _savedModelsCount: MutableLiveData<Int> = MutableLiveData()
    val savedModelsCount: LiveData<Int> = _savedModelsCount


    fun achievementWasViewed(achievement: UserAchievement) {
        viewModelScope.launch {
            achievement.wasViewed = true
            databaseRepository.saveAchievement(achievement)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
            databaseRepository.getAchievements(TEST_USER).collect {
                it.forEach { userAchievement ->
                    decodeSubjectItemImageUrl(userAchievement)
                }
            }
        }
    }

    private fun decodeSubjectItemImageUrl(achievement: UserAchievement) {
        achievement.apply {
            if (this.iconByteArray.isEmpty()) {
                val data = Data.Builder()
                    .putInt(KEY_DATABASE_TYPE, ACHIEVEMENT_DATABASE)
                    .putInt(KEY_PRIMARY_KEY, this.achievementPrimaryKey)
                    .build()
                val workRequest = OneTimeWorkRequestBuilder<ImageUrlDecoderWorker>()
                    .setInputData(data)
                    .build()
                workManager.enqueue(workRequest)
            }
        }
    }

    init {
        viewModelScope.launch {
            databaseRepository.getAllPassedUserTests(TEST_USER).collect {
                _doneTestsCount.postValue(it.size)
            }
        }
        viewModelScope.launch {
            databaseRepository.getAllModelsFlow("76V1UE5VssV0W8mXenibeUpvQxm1").collect {
                _savedModelsCount.postValue(it.size)
            }
        }
        viewModelScope.launch {
            databaseRepository.getProgress(TEST_USER).collect {
                it.count { userProgress -> userProgress.isLectureReaden }
                    .also { count -> _readenLecturesCount.postValue(count) }
            }
        }
    }

    companion object {
        private const val KEY_PRIMARY_KEY = "key.achievement.primary.key"
        private const val KEY_DATABASE_TYPE = "key.database.type"
    }
}