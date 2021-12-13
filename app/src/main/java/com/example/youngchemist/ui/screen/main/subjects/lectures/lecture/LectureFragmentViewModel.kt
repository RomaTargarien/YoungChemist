package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _isPaginationVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPaginationVisible: LiveData<Boolean> = _isPaginationVisible

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

    fun saveProgress(userProgress: UserProgress?,lastPage: Int) {
        viewModelScope.launch {
            userProgress?.let {
                if (it.lastReadenPage < lastPage) {
                    it.lastReadenPage = lastPage
                    databaseRepository.saveProgress(it)
                }
            }
        }
    }

    fun lectureHasBeenReaden(lectureId: String) {
        viewModelScope.launch {
            fireStoreRepository.updateReadenLectures(lectureId)
        }
    }
}