package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Test
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.ui.util.Constants.TEST_USER
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureFragmentViewModel @Inject constructor(
    private val router: Router,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _isPaginationVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPaginationVisible: LiveData<Boolean> = _isPaginationVisible

    private val _hasTheTestBeenDoneFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasTheTestBeenDoneFlow: StateFlow<Boolean> = _hasTheTestBeenDoneFlow

    fun getTest(test: Test) {
        viewModelScope.launch {
            databaseRepository.getAllPassedUserTests(TEST_USER).collect {
                _hasTheTestBeenDoneFlow.emit(it.map { it.testUid }.contains(test.testId))
            }
        }
    }

    fun exit() {
        router.exit()
    }

    fun navigateToTestScreen() {
        //router.replaceScreen(Screens.rootTestScreen(""))
    }


    fun togglePagePaginationVisibility() {
        _isPaginationVisible.value?.let {
            _isPaginationVisible.postValue(!it)
        }
    }

    fun saveProgress(userProgress: UserProgress?, lastPage: Int, lectureSize: Int) {
        viewModelScope.launch {
            userProgress?.let {
                if (it.lastReadenPage < lastPage) {
                    it.lastReadenPage = lastPage
                    it.isLectureReaden = lectureSize == lastPage
                    databaseRepository.saveProgress(it)
                }
            }
        }
    }
}