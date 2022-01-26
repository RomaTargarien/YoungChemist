package com.example.youngchemist.ui.screen.main.achievements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.youngchemist.model.Achievement
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.base.workers.ImageUrlDecoderWorker
import com.example.youngchemist.ui.base.workers.UserInfoDonloadingWorker
import com.example.youngchemist.ui.screen.main.MainFragmentViewModel
import com.example.youngchemist.ui.util.Constants
import com.example.youngchemist.ui.util.Constants.TEST_USER
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
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
            val data = Data.Builder().putInt(KEY_ACHIEVEMENT_PRIMARY_KEY,achievement.achievementPrimaryKey).build()
            val workRequest = OneTimeWorkRequestBuilder<ImageUrlDecoderWorker>().setInputData(data).build()
            workManager.enqueue(workRequest)
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
                it.count { it.isLectureReaden }.also { _readenLecturesCount.postValue(it) }
            }
        }
    }

    companion object {
        private const val KEY_ACHIEVEMENT_PRIMARY_KEY = "key.achievement.primary.key"
    }
}