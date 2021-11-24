package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Page
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _pagesStae: MutableLiveData<List<Page>> = MutableLiveData()
    val pagesState: LiveData<List<Page>> = _pagesStae

    private val _isPaginationVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPaginationVisible: LiveData<Boolean> = _isPaginationVisible

    fun getContent(subjectTitle: String, lectureTitle: String) {
        viewModelScope.launch {
            val lecture = databaseRepository.getAllPages(lectureTitle, subjectTitle)[0]
            _pagesStae.postValue(lecture.pages)
        }
    }
    fun exit() {
        router.exit()
    }

    fun navigateToTestScreen() {
        router.replaceScreen(Screens.testScreen())
    }

    fun togglePagePaginationVisibility() {
        _isPaginationVisible.value?.let {
            _isPaginationVisible.postValue(!it)
        }
    }
}