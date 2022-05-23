package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.model.user.UserProgress
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture_base.BaseLaunchTestViewModel
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class LectureFragmentViewModel @Inject constructor(
    private val router: Router,
    private val databaseRepository: DatabaseRepository,
    private val currentUser: FirebaseUser
) : BaseLaunchTestViewModel(router) {

    private val _isPaginationVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPaginationVisible: LiveData<Boolean> = _isPaginationVisible

    private val lastPage: MutableStateFlow<Int> = MutableStateFlow(1)

    private val _hasTheTestBeenDoneFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val hasTheTestBeenDoneFlow: StateFlow<Boolean> = _hasTheTestBeenDoneFlow

    fun getTest(test: Test) {
        viewModelScope.launch {
            databaseRepository.getAllPassedUserTests(currentUser.uid).collect {
                _hasTheTestBeenDoneFlow.emit(
                    it.map { it.testUid }.contains(test.testId)
                )
            }
        }
    }

    fun onPageChanged(page: Int) {
        if (lastPage.value < page + 1) {
            lastPage.value = page + 1
        }
    }

    fun exit() {
        router.exit()
    }

    fun togglePagePaginationVisibility() {
        _isPaginationVisible.value?.let {
            _isPaginationVisible.postValue(!it)
        }
    }

    fun saveProgress(userProgress: UserProgress?, lectureSize: Int) {
        viewModelScope.launch {
            userProgress?.let {
                if (it.lastReadenPage < lastPage.value) {
                    it.lastReadenPage = lastPage.value
                    it.isLectureReaden = lectureSize == lastPage.value
                    databaseRepository.saveProgress(it)
                }
            }
        }
    }
}