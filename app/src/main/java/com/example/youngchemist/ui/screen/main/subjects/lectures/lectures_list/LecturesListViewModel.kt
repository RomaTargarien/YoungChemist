package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.ui.LectureUi
import com.example.youngchemist.model.Test
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.getAmountOffTests
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LecturesListViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository,
) : ViewModel() {

    private val _lecturesListState: MutableLiveData<ResourceNetwork<List<Lecture>>> =
        MutableLiveData()
    val lecturesListState: LiveData<ResourceNetwork<List<Lecture>>> = _lecturesListState

    private val _lecturesUi: MutableLiveData<List<LectureUi>> = MutableLiveData()
    val lecturesUi: LiveData<List<LectureUi>> = _lecturesUi

    private val _doneTests: MutableLiveData<Pair<Int,Int>> = MutableLiveData()
    val doneTests: LiveData<Pair<Int,Int>> = _doneTests

    fun getData(collectionId: String) {
        viewModelScope.launch {
            val lecturesUi = mutableListOf<LectureUi>()
            _lecturesListState.postValue(ResourceNetwork.Loading())
            val result = databaseRepository.getAllLectures(collectionId)
            val userResult = fireStoreRepository.getUser("dJuRGOc06xhllmscaAEqQoHC9Ir2")
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
            val user = userResult.await()
            if (user is ResourceNetwork.Success) {
                user.data?.let { userData ->
                    for (item in lecturesUi) {
                        item.test?.let {
                            item.isTestEnabled = !(it.testId in userData.passedTestsId())
                            item.mark = userData.passedTestMark(it.testId)
                        }
                        item.lectureWasReaden = item.lectureId in userData.readenLectures
                    }
                    _lecturesUi.postValue(lecturesUi)
                }
            } else {
                _lecturesUi.postValue(lecturesUi)
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