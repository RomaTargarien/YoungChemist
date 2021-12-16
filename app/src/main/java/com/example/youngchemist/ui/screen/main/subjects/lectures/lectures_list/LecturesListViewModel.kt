package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.util.Log
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Test
import com.example.youngchemist.model.ui.LectureUi
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Constants.TEST_USER
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.getLecturesId
import com.example.youngchemist.ui.util.getLocalLecturesId
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LecturesListViewModel @Inject constructor(
    private val router: Router,
    private val workManager: WorkManager,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _lecturesListState: MutableLiveData<ResourceNetwork<List<Lecture>>> =
        MutableLiveData()
    val lecturesListState: LiveData<ResourceNetwork<List<Lecture>>> = _lecturesListState

    private val _lecturesUi: MutableLiveData<List<LectureUi>> = MutableLiveData()
    val lecturesUi: LiveData<List<LectureUi>> = _lecturesUi

    private val _doneTests: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val doneTests: LiveData<Pair<Int, Int>> = _doneTests

    private val userProgresshasBeenApproved = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            userPreferences.loggedUserState.asFlow().filterNotNull().collect {
                if (TEST_USER in it) {
                    userProgresshasBeenApproved.emit(true)
                }
            }
        }
    }

    fun getData(collectionId: String) {
        viewModelScope.launch {
            val lecturesUi = mutableListOf<LectureUi>()
            _lecturesListState.postValue(ResourceNetwork.Loading())
            val result = databaseRepository.getAllLectures(collectionId)
            val progressList = databaseRepository.getProgress(TEST_USER)
            val passedUserTests = databaseRepository.getAllPassedUserTests(TEST_USER)
            var data = result.await()

            if (data.isEmpty()) {
                fireStoreRepository.getAllLectures(collectionId).let {
                    if (it is ResourceNetwork.Success) {
                        it.data?.let {
                            data = it
                            databaseRepository.insertNewLectures(it)
                        }
                        _lecturesListState.postValue(it)
                    }
                    if (it is ResourceNetwork.Error) {
                        _lecturesListState.postValue(it)
                    }
                }
            } else {
                _lecturesListState.postValue(ResourceNetwork.Success(emptyList()))
            }
            data.forEach {
                lecturesUi.add(it.convertToLectureUi())
            }
            _lecturesUi.postValue(lecturesUi)
            launch {
                combine(userProgresshasBeenApproved,progressList){ a, b ->
                    Pair(a,b)
                }.collect {
                    val available = it.first
                    val userProgress = it.second
                    if (available) {
                        lecturesUi.forEach {
                            addUserProgress(it,userProgress)
                        }
                        _lecturesUi.postValue(lecturesUi)
                    }

                }
            }
            launch {
                combine(userProgresshasBeenApproved,passedUserTests) { a, b ->
                    Pair(a,b)
                }.collect {
                    val available = it.first
                    val passedTests = it.second
                    if (available) {
                        lecturesUi.forEach {
                            addUserPassedTests(it,passedTests)
                        }
                        _lecturesUi.postValue(lecturesUi)
                    }
                }
            }
        }
    }



    private fun addUserProgress(lectureUi: LectureUi,userProgressList: List<UserProgress>) {
        viewModelScope.launch {
            if (!(lectureUi.lectureId in userProgressList.getLocalLecturesId())) {
                val userProgress = UserProgress(TEST_USER,lectureUi.lectureId,0)
                lectureUi.userProgress = userProgress
                databaseRepository.saveProgress(userProgress)
            } else {
                lectureUi.userProgress = userProgressList.find { userProgress ->
                    userProgress.lectureId.equals(lectureUi.lectureId)
                }
            }
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

    fun exit() {
        router.exit()
    }

    fun navigateToLectureScreen(lecture: LectureUi) {
        router.navigateTo(Screens.lectureScreen(lecture))
    }

    fun navigateToTestScreen(test: Test) {
        router.navigateTo(Screens.rootTestScreen(test))
    }
}