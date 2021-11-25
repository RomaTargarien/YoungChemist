package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.os.CountDownTimer
import android.util.Log
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
) : ViewModel() {

    private val _testState: MutableLiveData<ResourceNetwork<Test>> = MutableLiveData()
    val testState: LiveData<ResourceNetwork<Test>> = _testState

    private val _timeLeft: MutableLiveData<String> = MutableLiveData()
    val timeLeft: LiveData<String> = _timeLeft

    init {
        viewModelScope.launch {
            val result = fireStoreRepository.retriveTest("Zhw6FlebgEIjh7D4eEGx")
            _testState.postValue(result)
        }
        val countDownTimer = object : CountDownTimer(900000, 1000) {
            override fun onTick(p0: Long) {
                val minutes = p0 / 1000 / 60
                val seconds = ((p0 / 1000) % 60)
                _timeLeft.postValue("${if (minutes < 10) "0$minutes" else minutes}" + ":" +
                            "${if (seconds < 10) "0$seconds" else seconds}"
                )
            }

            override fun onFinish() {
                Log.d("TAG", "finish")
            }
        }
        countDownTimer.start()
    }
}