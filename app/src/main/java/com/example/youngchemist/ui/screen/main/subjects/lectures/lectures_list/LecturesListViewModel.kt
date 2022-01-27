package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Test
import com.example.youngchemist.model.ui.LectureUi
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Constants.TEST_USER
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LecturesListViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _lecturesUiState: MutableLiveData<ResourceNetwork<List<LectureUi>>> =
        MutableLiveData()
    val lecturesUiState: LiveData<ResourceNetwork<List<LectureUi>>> = _lecturesUiState

    private val _passedTestsCount: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val passedTestsCount: LiveData<Pair<Int, Int>> = _passedTestsCount

    private val _readenLecturesCount: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val readenLecturesCount: LiveData<Pair<Int, Int>> = _readenLecturesCount

    fun exit() {
        router.exit()
    }

    fun navigateToLectureScreen(lecture: LectureUi) {
        router.navigateTo(Screens.lectureScreen(lecture))
    }

    fun navigateToTestScreen(test: Test) {
        router.navigateTo(Screens.rootTestScreen(test))
    }

    fun getLectures(collectionId: String) {
        viewModelScope.launch {
            _lecturesUiState.postValue(ResourceNetwork.Loading())
            val progressList = databaseRepository.getProgress(TEST_USER)
            val passedUserTests = databaseRepository.getAllPassedUserTests(TEST_USER)
            val lectures = databaseRepository.getLectures(collectionId)
            combine(lectures, passedUserTests, progressList) { lecture, tests, progress ->
                Triple(lecture, tests, progress)
            }.onEach {
                if (it.first.isEmpty()) {
                    loadRemoteLectures(collectionId)
                }
            }.filterNot {
                it.first.isEmpty()
            }.collect {
                val savedLectures = it.first
                val passedTests = it.second
                val userProgress = it.third
                val lecturesUi = savedLectures.map { it.convertToLectureUi() }
                lecturesUi.forEach {
                    addUserProgress(it, userProgress)
                    addUserPassedTests(it, passedTests)
                }
                userProgressCounts(lecturesUi)
                _lecturesUiState.postValue(ResourceNetwork.Success(lecturesUi))
            }
        }
    }

    private fun loadRemoteLectures(collectionId: String) {
        viewModelScope.launch {
            val result = fireStoreRepository.getAllLectures(collectionId)
            when (result) {
                is ResourceNetwork.Success -> {
                    result.data?.forEach {
                        databaseRepository.saveLecture(it)
                    }
                }
                is ResourceNetwork.Error -> {
                    _lecturesUiState.postValue(ResourceNetwork.Error(result.message))
                }
            }
        }
    }

    private fun userProgressCounts(lecturesUi: List<LectureUi>) {
        val allAmountOfTests = lecturesUi.count { it.test != null }
        val doneTests = lecturesUi.count { it.test != null && !it.isTestEnabled }
        val allAmountsOfLectures = lecturesUi.size
        var readLectures = 0
        lecturesUi.forEach {
            it.userProgress?.let {
                if (it.isLectureReaden) {
                    readLectures++
                }
            }
        }
        _passedTestsCount.postValue(Pair(allAmountOfTests, doneTests))
        _readenLecturesCount.postValue(Pair(allAmountsOfLectures, readLectures))
    }

    private fun addUserProgress(lectureUi: LectureUi, userProgressList: List<UserProgress>) {
        userProgressList.find { userProgress ->
            userProgress.lectureId == lectureUi.lectureId
        }.also {
            if (it == null) {
                saveProgress(UserProgress(TEST_USER, lectureUi.lectureId, 0))
            } else {
                lectureUi.userProgress = it
            }
        }
    }

    private fun saveProgress(userProgress: UserProgress) {
        viewModelScope.launch {
            databaseRepository.saveProgress(userProgress)
        }
    }

    private fun addUserPassedTests(lectureUi: LectureUi,userPassedTests: List<PassedUserTest>) {
        lectureUi.test?.let { test ->
            lectureUi.isTestEnabled = true
            val passedUserTest = userPassedTests.find { passedUserTest ->
                test.testId.equals(passedUserTest.testUid)
            }
            passedUserTest?.let {
                lectureUi.isTestEnabled = false
                lectureUi.mark = it.mark
            }
        }
    }
}