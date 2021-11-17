package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Lection
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LecturesListViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {

    private val _lecturesListState: MutableLiveData<ResourceNetwork<List<Lection>>> = MutableLiveData()
    val lecturesListState: LiveData<ResourceNetwork<List<Lection>>> = _lecturesListState

    fun getAllLectures(subjectTitle: String) {
        viewModelScope.launch {
            val result = fireStoreRepository.getAllLectures(subjectTitle)
            _lecturesListState.postValue(result)
        }
    }

    fun navigateToLectureScreen(subjectTitle: String,lectureTitle: String) {
        router.navigateTo(Screens.lectureScreen(lectureTitle, subjectTitle))
    }

}