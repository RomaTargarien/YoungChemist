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
import com.example.youngchemist.ui.util.ResourceNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureFragmentViewModel @Inject constructor(
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _uriState: MutableLiveData<ResourceNetwork<Uri>> = MutableLiveData()
    val uriState: LiveData<ResourceNetwork<Uri>> = _uriState

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

    fun get3DModelUri(fileName: String) {
        viewModelScope.launch {
            val result = fireStoreRepository.get3DModel(fileName)
            _uriState.postValue(result)
        }
    }

    fun togglePagesPaginationVisibility() {
        Log.d("TAG","Unit")
        _isPaginationVisible.value?.let {
            _isPaginationVisible.postValue(!it)
        }
    }
}