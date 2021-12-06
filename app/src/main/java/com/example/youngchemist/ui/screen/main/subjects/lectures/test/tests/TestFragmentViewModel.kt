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
import com.example.youngchemist.model.*
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.*
import com.example.youngchemist.ui.util.TestExitBehavior.*
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    @ApplicationContext val context: Context,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _tasksUi: MutableStateFlow<MutableList<TaskUi>> = MutableStateFlow(mutableListOf())
    val tasksUi: StateFlow<MutableList<TaskUi>> = _tasksUi

    private val _test: MutableStateFlow<Test?> = MutableStateFlow(null)
    val test: StateFlow<Test?> = _test

    private val saveAndShowMark: MutableStateFlow<Pair<Boolean,Boolean>> = MutableStateFlow(Pair(false,false))

    private val _exitBehavior: MutableLiveData<TestExitBehavior> = MutableLiveData()
    val exitBehavior: LiveData<TestExitBehavior> = _exitBehavior

    private val _currentPage: MutableLiveData<Pair<Int,FragmentAnimationBehavior>> = MutableLiveData(
        Pair(-1,FragmentAnimationBehavior.Enter())
    )
    val currentPage: LiveData<Pair<Int,FragmentAnimationBehavior>> = _currentPage

    private val _testState: MutableLiveData<Event<ResourceNetwork<Test>>> = MutableLiveData()
    val testState: LiveData<Event<ResourceNetwork<Test>>> = _testState

    private val _timeLeft: MutableLiveData<String> = MutableLiveData()
    val timeLeft: LiveData<String> = _timeLeft

    private val _timeIsUp: MutableLiveData<Boolean> = MutableLiveData()
    val timeIsUp: LiveData<Boolean> = _timeIsUp

    private var exitState: TestExitBehavior
    private lateinit var countDownTimer: CountDownTimer


    init {
        exitState = Exit()
        observeSavingState()

    }

    fun retrieveTest(testId: String) {
        viewModelScope.launch {
            Log.d("TAG",testId)
            _testState.postValue(Event(ResourceNetwork.Loading()))
            val result = fireStoreRepository.retriveTest(testId)
            Log.d("TAG",result.data.toString())
            if (result is ResourceNetwork.Success) {
                exitState = ExitNoSave()
                result.data?.let {
                    initializeCountDownTimer(it.timeInMillis)
                    _tasksUi.emit(initializeEmptyTaskUiList(it.tasks))
                    _test.emit(it)
                    countDownTimer.start()
                }
            }
            _testState.postValue(Event(result))
        }
    }

    fun udpateTasksUi(pageNumber: Int,answersUi: List<AnswerUi>) {
        _tasksUi.value[pageNumber].answersList = answersUi
    }

    private fun observeSavingState() {
        viewModelScope.launch(Dispatchers.Default) {
            combine(test, tasksUi, saveAndShowMark) { test, tasks, save ->
                Triple(test, tasks, save)
            }.collect {
                val save = it.third.first
                val saveWithNoProgress = it.third.second
                if (save) {
                    val test = it.first!!
                    val tasks = it.second
                    val testUi = TestUi(
                        "",
                        test.testId,
                        if (saveWithNoProgress) ArrayList(initializeEmptyTaskUiList(test.tasks)) else ArrayList(tasks)
                    )
                    val passedUserTest = testUi.formatUserPassedTest(test)
                    uploadTest(passedUserTest,saveWithNoProgress)
                }
            }
        }
    }

    fun saveTest(save: Boolean,saveWithNoProgress: Boolean) {
        viewModelScope.launch {
            saveAndShowMark.emit(Pair(save,saveWithNoProgress))
        }
    }

    private fun uploadTest(passedUserTest: PassedUserTest,saveWithNoProgress: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            if (isOnline(context)) {
                fireStoreRepository.saveTest("dJuRGOc06xhllmscaAEqQoHC9Ir2",passedUserTest)
            }
            databaseRepository.savePassedUserTest(passedUserTest)
            navigate(saveWithNoProgress, passedUserTest)
        }
    }

    private fun navigate(saveWithNoProgress: Boolean,passedUserTest: PassedUserTest) {
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
            _currentPage.postValue(Pair(++page,FragmentAnimationBehavior.Enter()))
        }
    }

    fun goForward() {
        currentPage.value?.let {
            var page = it.first
            _currentPage.postValue(Pair(++page,FragmentAnimationBehavior.Forward()))
        }
    }

    fun goBack() {
        currentPage.value?.let {
            var page = it.first
            _currentPage.postValue(Pair(--page,FragmentAnimationBehavior.Back()))
        }
    }

    fun tryExitTheTest() {
        _exitBehavior.postValue(exitState)
    }

    fun exit() {
        router.exit()
    }

    fun done() {
        _exitBehavior.postValue(ExitSave())
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
                val answerUi = AnswerUi(k,false)
                answersList.add(answerUi)
            }
            taskUi.answersList = answersList
            emptyTaskUiList.add(taskUi)
        }
        return emptyTaskUiList
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            } else {
                null
            }
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.d("TAG", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.d("TAG", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.d("TAG", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}