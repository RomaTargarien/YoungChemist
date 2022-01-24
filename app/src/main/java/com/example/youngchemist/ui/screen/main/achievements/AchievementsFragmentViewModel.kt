package com.example.youngchemist.ui.screen.main.achievements

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Achievement
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.Constants.TEST_USER
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _achievements: MutableStateFlow<List<Achievement>?> = MutableStateFlow(null)
    val achievements: StateFlow<List<Achievement>?> = _achievements

    private val _userAchievements: MutableLiveData<List<UserAchievement>> = MutableLiveData()
    val userAchievements: LiveData<List<UserAchievement>> = _userAchievements


    fun getAchievements() {
        viewModelScope.launch {
            val result = fireStoreRepository.getAllAchivements()
            when (result) {
                is ResourceNetwork.Success -> {
                    result.data?.let {
                        _achievements.emit(it)
                    }
                }
                is ResourceNetwork.Error -> {

                }
            }
        }
    }

    init {
        viewModelScope.launch {
            val passedUserTests = databaseRepository.getAllPassedUserTests(TEST_USER)
            val userModels = databaseRepository.getAllModelsFlow(TEST_USER)
            val userProgress = databaseRepository.getProgress(TEST_USER)
            combine(
                passedUserTests,
                userModels,
                userProgress,
                achievements
            ) { tests, models, progress, achievements ->
                ResultList(tests, models, progress, achievements)
            }.filterNot {
                it.achievementList == null
            }.collect { resultList ->
                val userAchievementsList = mutableListOf<UserAchievement>()
                resultList.achievementList!!.forEach { achievement ->
                    val userAchievement = achievement.convertToUserAchievement()
                    Log.d("TAG",achievement.toString())
                    achievement.isRegistered?.let {
                        Log.d("TAG","register")
                        userAchievement.itemsDone = 1
                    }

                    achievement.testsToDone?.let { testsToDone ->
                        val testsDone = resultList.testList

                        userAchievement.itemsDone =
                            if (testsToDone > testsDone.size) testsDone.size else testsToDone

                        achievement.testsMark?.let { mark ->
                            resultList.testList.filterNot {
                                it.mark <= mark
                            }.also {
                                userAchievement.itemsDone =
                                    if (testsToDone > it.size) it.size else testsToDone
                            }
                        }

                        achievement.testsAverageMark?.let { avarageMark ->
                            if (testsToDone >= testsDone.size)
                                resultList.testList.sumOf {
                                    it.mark
                                }.also {
                                    val currentAvarageMark = it / testsDone.size
                                }
                        }

                        achievement.modelsToSave?.let { modelsToSave ->
                            val modelsSaved = resultList.modelsList
                            userAchievement.itemsDone =
                                if (modelsToSave > modelsSaved.size) modelsSaved.size else modelsToSave
                        }
                    }
                    achievement.lecturesToRead?.let {

                    }
                    userAchievementsList.add(userAchievement)
                }
                _userAchievements.postValue(userAchievementsList)
            }
        }
    }

    inner class ResultList(
        val testList: List<PassedUserTest>,
        val modelsList: List<Model3D>,
        val progressList: List<UserProgress>,
        val achievementList: List<Achievement>?
    ) {
        override fun toString(): String {
            return testList.toString() + modelsList.toString() + progressList.toString() + achievementList.toString()
        }
    }
}