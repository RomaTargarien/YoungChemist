package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.model.ui.LectureUi
import com.chemist.youngchemist.model.user.UserProgress
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.chemist.youngchemist.repositories.FireStoreRepository
import com.chemist.youngchemist.ui.screen.Screens
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture_base.BaseLaunchTestViewModel
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
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

    private var subjectCollectionId: String = ""
    private val lecturesIds: MutableSet<String> = mutableSetOf()

    private val _numberOfLecturesDownloaded: MutableSharedFlow<Int?> = MutableSharedFlow()
    val numberOfLecturesDownloaded: SharedFlow<Int?> = _numberOfLecturesDownloaded

    fun exit() {
        router.exit()
    }

    fun navigateToLectureScreen(lecture: LectureUi) {
        router.navigateTo(Screens.lectureScreen(lecture))
    }

    fun getLectures(collectionId: String) {
        viewModelScope.launch {
            subjectCollectionId = collectionId
            _lecturesUiState.postValue(ResourceNetwork.Loading())
            val progressList = databaseRepository.getProgress(currentUser.uid)
            val passedUserTests = databaseRepository.getAllPassedUserTests(currentUser.uid)
            val lectures = databaseRepository.getLectures(collectionId)
            combine(lectures, passedUserTests, progressList) { lecture, tests, progress ->
                Triple(lecture, tests, progress)
            }.onEach {
                if (it.first.isEmpty()) {
                    loadRemoteLectures()
                }
            }.filterNot {
                it.first.isEmpty()
            }.collect {
                val savedLectures = it.first
                val passedTests = it.second
                val userProgress = it.third
                val lecturesUi = savedLectures.map { it.convertToLectureUi() }
                lecturesUi.forEach { lectureUi ->
                    lecturesIds.add(lectureUi.lectureId)
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

    fun loadRemoteLectures() {
        viewModelScope.launch {
            val result = fireStoreRepository.getAllLectures(subjectCollectionId)
            when (result) {
                is ResourceNetwork.Success -> {
                    var newLecturesNumber = 0
                    result.data?.forEach {
                        if (it.lectureId !in lecturesIds) {
                            newLecturesNumber++
                            databaseRepository.saveLecture(it)
                            lecturesIds.add(it.lectureId)
                        }
                    }
                    _numberOfLecturesDownloaded.emit(newLecturesNumber)
                }
                is ResourceNetwork.Error -> {
                    _numberOfLecturesDownloaded.emit(null)
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