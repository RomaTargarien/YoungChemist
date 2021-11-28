package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.*
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Event
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository
) : ViewModel() {

    var test: Test? = null
    var tasksUi: MutableList<TaskUi> = mutableListOf()

    private val _currentPage: MutableLiveData<Int> = MutableLiveData(-1)
    val currentPage: LiveData<Int> = _currentPage

    private val _testState: MutableLiveData<Event<ResourceNetwork<Test>>> = MutableLiveData()
    val testState: LiveData<Event<ResourceNetwork<Test>>> = _testState

    private val _timeLeft: MutableLiveData<String> = MutableLiveData()
    val timeLeft: LiveData<String> = _timeLeft

    init {
        viewModelScope.launch {
            val result = fireStoreRepository.retriveTest("Ub8gvb0ZbjWjK3o2Z3ke")
            if (result is ResourceNetwork.Success) {
                result.data?.let {
                    test = it
                    initializeEmptyTaskUiList(it.tasks)
                }
            }
            _testState.postValue(Event(result))
        }
        val countDownTimer = object : CountDownTimer(900000, 1000) {
            override fun onTick(p0: Long) {
                val minutes = p0 / 1000 / 60
                val seconds = ((p0 / 1000) % 60)
                _timeLeft.postValue(
                    "${if (minutes < 10) "0$minutes" else minutes}" + ":" +
                            "${if (seconds < 10) "0$seconds" else seconds}"
                )
            }

            override fun onFinish() {
                Log.d("TAG", "finish")
            }
        }
        countDownTimer.start()
    }

    fun saveTest() {
        viewModelScope.launch {

        }
    }

    fun goForward() {
        currentPage.value?.let {
            var page = it
            _currentPage.postValue(++page)
        }
    }

    fun goBack() {
        currentPage.value?.let {
            var page = it
            _currentPage.postValue(--page)
        }
    }

    private fun initializeEmptyTaskUiList(tasks: ArrayList<Task>) {
        for (i in 0 until tasks.size) {
            val taskUi = TaskUi(i)
            val answersList = arrayListOf<AnswerUi>()
            for (k in 0 until tasks[i].answers.size) {
                val answerUi = AnswerUi(k,false)
                answersList.add(answerUi)
            }
            taskUi.answersList = answersList
            tasksUi.add(taskUi)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TAG","1oncleraed")
    }
}