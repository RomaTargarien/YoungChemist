package com.chemist.youngchemist.ui.screen.main.subjects.lectures.test.tests

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.chemist.youngchemist.model.Task
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.model.ui.AnswerUi
import com.chemist.youngchemist.model.ui.TaskUi
import com.chemist.youngchemist.model.ui.TestUi
import com.chemist.youngchemist.model.user.PassedUserTest
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.chemist.youngchemist.ui.base.workers.TestUploadingWorker
import com.chemist.youngchemist.ui.screen.Screens
import com.chemist.youngchemist.ui.util.FragmentAnimationBehavior
import com.chemist.youngchemist.ui.util.evaluateTime
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class TestFragmentViewModel @Inject constructor(
    private val router: Router,
    private val workManager: WorkManager,
    private val databaseRepository: DatabaseRepository,
    private val currentUser: FirebaseUser
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
                        currentUser.uid,
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

    private fun enterTest() {
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