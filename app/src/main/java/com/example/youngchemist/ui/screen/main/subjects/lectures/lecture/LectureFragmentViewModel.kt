package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Content
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {

    private val _contentState: MutableLiveData<ResourceNetwork<Content>> = MutableLiveData()
    val contentState: LiveData<ResourceNetwork<Content>> = _contentState

    fun getContent(subjectTitle: String,lectureTitle: String) {
        viewModelScope.launch {
            val result = fireStoreRepository.getLecture(subjectTitle, lectureTitle)
            _contentState.postValue(result)
            Log.d("TAG",result.data.toString())
        }
    }
}