package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.repositories.DatabaseRepository
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
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
): ViewModel() {

    private val _lecturesListState: MutableLiveData<ResourceNetwork<List<Lecture>>> = MutableLiveData()
    val lecturesListState: LiveData<ResourceNetwork<List<Lecture>>> = _lecturesListState

    fun getAllLectures(subjectTitle: String) {
        viewModelScope.launch {
            val lectures = databaseRepository.getAllLectures(subjectTitle)
            Log.d("TAG",lectures.toString())
            if (databaseRepository.getAllLectures(subjectTitle).isEmpty()) {
                _lecturesListState.postValue(ResourceNetwork.Loading())
                val result = fireStoreRepository.getAllLectures(subjectTitle)
                if (result is ResourceNetwork.Success) {
                    result.data?.let {
                        databaseRepository.insertNewLectures(it)
                    }
                }
                _lecturesListState.postValue(result)
            } else {
                val lectures = databaseRepository.getAllLectures(subjectTitle)
                _lecturesListState.postValue(ResourceNetwork.Success(lectures))
            }
        }
    }

    fun navigateToLectureScreen(subjectTitle: String,lectureTitle: String) {
        router.navigateTo(Screens.lectureScreen(lectureTitle, subjectTitle))
    }

}