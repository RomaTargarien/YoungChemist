package com.chemist.youngchemist.ui.screen.main.achievements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.model.user.UserAchievement
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsFragmentViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val currentUser: FirebaseUser
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
        viewModelScope.launch {
            databaseRepository.getAllPassedUserTests(currentUser.uid).collect {
                _doneTestsCount.postValue(it.size)
            }
        }
        viewModelScope.launch {
            databaseRepository.getAllModelsFlow(currentUser.uid).collect {
                _savedModelsCount.postValue(it.size)
            }
        }
        viewModelScope.launch {
            databaseRepository.getProgress(currentUser.uid).collect {
                it.count { userProgress -> userProgress.isLectureReaden }
                    .also { count -> _readenLecturesCount.postValue(count) }
            }
        }
    }
}