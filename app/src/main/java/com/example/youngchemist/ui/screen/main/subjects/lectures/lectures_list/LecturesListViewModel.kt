package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.ui.LectureUi
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.screen.main.subjects.lectures.lecture_base.BaseLaunchTestViewModel
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class LecturesListViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository,
    private val currentUser: FirebaseUser
) : BaseLaunchTestViewModel(router) {

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

    fun getLectures(collectionId: String) {
        viewModelScope.launch {
            _lecturesUiState.postValue(ResourceNetwork.Loading())
            val progressList = databaseRepository.getProgress(currentUser.uid)
            val passedUserTests = databaseRepository.getAllPassedUserTests(currentUser.uid)
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
                lecturesUi.forEach { lectureUi ->
                    lectureUi.addUserPassedTests(passedTests)
                    lectureUi.addUserProgress(userProgress).also {
                        if (!it) { initializeNewProgressProgress(lectureUi.lectureId) }
                    }
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

    private fun initializeNewProgressProgress(lectureId: String) {
        viewModelScope.launch {
            val userProgress = UserProgress(currentUser.uid, lectureId, 0)
            databaseRepository.saveProgress(userProgress)
        }
    }
}