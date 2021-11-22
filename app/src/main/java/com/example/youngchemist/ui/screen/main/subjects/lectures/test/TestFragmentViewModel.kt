package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Test
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository
): ViewModel() {

    private val _testState: MutableLiveData<ResourceNetwork<Test>> = MutableLiveData()
    val testState: LiveData<ResourceNetwork<Test>> = _testState

    init {
        viewModelScope.launch {
//            val result = fireStoreRepository.retriveTest("2uB7LeuEN1fmZLFI1c7n")
//            _testState.postValue(result)
        }
    }
}