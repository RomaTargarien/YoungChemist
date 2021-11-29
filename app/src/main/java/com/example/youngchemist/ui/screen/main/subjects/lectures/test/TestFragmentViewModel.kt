package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.*
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Event
import com.example.youngchemist.ui.util.FragmentAnimationBehavior
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
@HiltViewModel
class TestFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    @ApplicationContext val context: Context,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    var test: Test? = null
    var tasksUi: MutableList<TaskUi> = mutableListOf()
    private lateinit var countDownTimer: CountDownTimer

    private val _currentPage: MutableLiveData<Pair<Int,FragmentAnimationBehavior>> = MutableLiveData(
        Pair(-1,FragmentAnimationBehavior.Enter())
    )
    val currentPage: LiveData<Pair<Int,FragmentAnimationBehavior>> = _currentPage

    private val _testState: MutableLiveData<Event<ResourceNetwork<Test>>> = MutableLiveData()
    val testState: LiveData<Event<ResourceNetwork<Test>>> = _testState

    private val _timeLeft: MutableLiveData<String> = MutableLiveData()
    val timeLeft: LiveData<String> = _timeLeft

    init {
        viewModelScope.launch {
            _testState.postValue(Event(ResourceNetwork.Loading()))
            val result = fireStoreRepository.retriveTest("Ub8gvb0ZbjWjK3o2Z3ke")
            if (result is ResourceNetwork.Success) {
                initializeCountDownTimer()
                result.data?.let {
                    test = it
                    initializeEmptyTaskUiList(it.tasks)
                    countDownTimer.start()
                }
            }
            _testState.postValue(Event(result))
        }
        Log.d("TAG",isOnline(context).toString())
    }

    fun saveTest() {
        viewModelScope.launch(Dispatchers.Default) {
            Log.d("TAG","save")
            val testUi = TestUi("","", ArrayList(tasksUi))
            test?.let {
                Log.d("TAG","1")
                val passedUserTest = testUi.formatUserPassedTest(it)
                Log.d("TAG","2")
                Log.d("TAG",isOnline(context).toString())
                val result = fireStoreRepository.saveTest("dJuRGOc06xhllmscaAEqQoHC9Ir2",passedUserTest)
                if (isOnline(context)) {

                    if (result is ResourceNetwork.Error) {

                    }
                } else {
                    Log.d("TAG","saving")
                    databaseRepository.savePassedUserTest(passedUserTest)
                }

                launch(Dispatchers.Main) {
                    router.replaceScreen(Screens.testResultScreen(passedUserTest.mark))
                }
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

    fun exit() {
        router.exit()
    }

    fun initializeCountDownTimer() {
        countDownTimer = object : CountDownTimer(900000, 1000) {
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
    }

    fun goBack() {
        currentPage.value?.let {
            var page = it.first
            _currentPage.postValue(Pair(--page,FragmentAnimationBehavior.Back()))
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


    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        Log.d("TAG","checking")
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
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
        Log.d("TAG",capabilities.toString())
        return false
    }
}