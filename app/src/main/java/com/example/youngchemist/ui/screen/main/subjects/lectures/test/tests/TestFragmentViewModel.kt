package com.example.youngchemist.ui.screen.main.subjects.lectures.test.tests

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.youngchemist.model.Task
import com.example.youngchemist.model.Test
import com.example.youngchemist.model.ui.AnswerUi
import com.example.youngchemist.model.ui.TaskUi
import com.example.youngchemist.model.ui.TestUi
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.base.workers.TestUploadingWorker
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Constants.TEST_USER
import com.example.youngchemist.ui.util.FragmentAnimationBehavior
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.evaluateTime
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestFragmentViewModel @Inject constructor(
    private val router: Router,
    private val workManager: WorkManager,
    @ApplicationContext val context: Context,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _test: MutableStateFlow<Test?> = MutableStateFlow(null)
    val test: StateFlow<Test?> = _test

    private val _tasksUi: MutableStateFlow<MutableList<TaskUi>> = MutableStateFlow(mutableListOf())
    val tasksUi: StateFlow<MutableList<TaskUi>> = _tasksUi

    private val saveAndShowMark: MutableStateFlow<Pair<Boolean, Boolean>> =
        MutableStateFlow(Pair(false, false))

    private val _currentPage: MutableLiveData<Pair<Int, FragmentAnimationBehavior>> =
        MutableLiveData(
            Pair(-1, FragmentAnimationBehavior.Enter())
        )
    val currentPage: LiveData<Pair<Int, FragmentAnimationBehavior>> = _currentPage

    private val _timeLeft: MutableLiveData<String> = MutableLiveData()
    val timeLeft: LiveData<String> = _timeLeft

    private val _timeIsUp: MutableLiveData<Boolean> = MutableLiveData()
    val timeIsUp: LiveData<Boolean> = _timeIsUp

    private lateinit var countDownTimer: CountDownTimer


    init {
        observeSavingState()
    }

    fun createUserTest(test: Test) {
        viewModelScope.launch {
            _tasksUi.emit(initializeEmptyTaskUiList(test.tasks))
            _test.emit(test)
            initializeCountDownTimer(test.timeInMillis)
            countDownTimer.start()
        }
        enterTest()
    }

    fun udpateTasksUi(pageNumber: Int, answersUi: List<AnswerUi>) {
        _tasksUi.value[pageNumber].answersList = answersUi
    }

    private fun observeSavingState() {
        viewModelScope.launch(Dispatchers.Default) {
            combine(_test, tasksUi, saveAndShowMark) { test, tasks, save ->
                Triple(test, tasks, save)
            }.collect {
                val save = it.third.first
                val saveWithNoProgress = it.third.second
                if (save) {
                    val test = it.first!!
                    val tasks = it.second
                    val testUi = TestUi(
                        TEST_USER,
                        test.testId,
                        if (saveWithNoProgress) ArrayList(initializeEmptyTaskUiList(test.tasks)) else ArrayList(
                            tasks
                        )
                    )
                    val passedUserTest = testUi.formatUserPassedTest(test)
                    uploadTest(passedUserTest, saveWithNoProgress)
                }
            }
        }
    }

    fun saveTest(save: Boolean, saveWithNoProgress: Boolean) {
        viewModelScope.launch {
            saveAndShowMark.emit(Pair(save, saveWithNoProgress))
        }
    }

    private fun uploadTest(passedUserTest: PassedUserTest, saveWithNoProgress: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            databaseRepository.savePassedUserTest(passedUserTest)
            val data = Data.Builder()
                .putString(KEY_USER_ID,passedUserTest.userUid)
                .putString(KEY_TEST_ID,passedUserTest.testUid)
                .build()
            workManager.enqueue(OneTimeWorkRequestBuilder<TestUploadingWorker>().setInputData(data).build())
            navigate(saveWithNoProgress, passedUserTest)
        }
    }

    private fun navigate(saveWithNoProgress: Boolean, passedUserTest: PassedUserTest) {
        viewModelScope.launch(Dispatchers.Main) {
            if (saveWithNoProgress) {
                router.exit()
            } else {
                router.replaceScreen(Screens.testResultScreen(passedUserTest.mark))
            }
        }
    }

    fun enterTest() {
        currentPage.value?.let {
            var page = it.first
            _currentPage.postValue(Pair(++page, FragmentAnimationBehavior.Enter()))
        }
    }

    fun goForward() {
        currentPage.value?.let {
            var page = it.first
            _currentPage.postValue(Pair(++page, FragmentAnimationBehavior.Forward()))
        }
    }

    fun goBack() {
        currentPage.value?.let {
            var page = it.first
            _currentPage.postValue(Pair(--page, FragmentAnimationBehavior.Back()))
        }
    }

    fun exit() {
        router.exit()
    }

    private fun initializeCountDownTimer(timeInMillis: Long) {
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(p0: Long) {
                _timeLeft.postValue(p0.evaluateTime())
            }

            override fun onFinish() {
                timeIsUp()
            }
        }
    }

    private fun timeIsUp() {
        viewModelScope.launch(Dispatchers.Default) {
            _timeIsUp.postValue(true)
            delay(3000)
            _timeIsUp.postValue(false)
        }
    }

    private fun initializeEmptyTaskUiList(tasks: ArrayList<Task>): ArrayList<TaskUi> {
        val emptyTaskUiList = arrayListOf<TaskUi>()
        for (i in 0 until tasks.size) {
            val taskUi = TaskUi(i)
            val answersList = arrayListOf<AnswerUi>()
            for (k in 0 until tasks[i].answers.size) {
                val answerUi = AnswerUi(k, false)
                answersList.add(answerUi)
            }
            taskUi.answersList = answersList
            emptyTaskUiList.add(taskUi)
        }
        return emptyTaskUiList
    }

    companion object {
        private const val KEY_USER_ID = "key.user"
        private const val KEY_TEST_ID = "key.test"
    }
}