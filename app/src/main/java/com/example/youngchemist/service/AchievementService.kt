package com.example.youngchemist.service

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.Achievement
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.BitmapUtils
import com.example.youngchemist.ui.util.Constants
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class AchievementService: Service(), CoroutineScope {

    private val binder = LocalBinder()

    @Inject
    lateinit var fireStoreRepository: FireStoreRepository

    @Inject
    lateinit var databaseRepository: DatabaseRepository

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob


    private val _achievements: MutableStateFlow<List<Achievement>?> = MutableStateFlow(null)
    val achievements: StateFlow<List<Achievement>?> = _achievements

    private val _doneAchievements: MutableLiveData<List<UserAchievement>> = MutableLiveData()
    val doneAchievements: LiveData<List<UserAchievement>> = _doneAchievements

    private val _unDoneAchievements: MutableLiveData<List<UserAchievement>> = MutableLiveData()
    val unDoneAchievements: LiveData<List<UserAchievement>> = _unDoneAchievements

    private val userAchievementsFlow: MutableStateFlow<List<UserAchievement>?> =
        MutableStateFlow(null)

    override fun onBind(intent: Intent): IBinder {
        observeAchivementsProgress()
        Log.d("TAG","bind")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TAG","service onCreate")
        getAchievements()
    }

    private fun saveAchievement(userAchievement: UserAchievement) {
        launch {
            databaseRepository.saveAchievement(userAchievement)
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("TAG","unbind")
        return super.onUnbind(intent)

    }

    override fun onDestroy() {
        coroutineJob.cancel()
        Log.d("TAG","Service destroyed")
        super.onDestroy()
    }

    private fun getAchievements() {
        launch {
            val result = fireStoreRepository.getAllAchivements()
            when (result) {
                is ResourceNetwork.Success -> {
                    result.data?.let {
                        _achievements.emit(it)
                    }
                }
                is ResourceNetwork.Error -> {

                }
            }
        }
    }

    private fun observeAchivementsProgress() {
        launch(Dispatchers.IO) {
            val passedUserTests = databaseRepository.getAllPassedUserTests(Constants.TEST_USER)
            val userModels = databaseRepository.getAllModelsFlow("76V1UE5VssV0W8mXenibeUpvQxm1")
            val userProgress = databaseRepository.getProgress(Constants.TEST_USER)
            combine(
                passedUserTests,
                userModels,
                userProgress,
                achievements
            ) { tests, models, progress, achievements ->
                ResultList(tests, models, progress, achievements)
            }.filterNot {
                it.achievementList == null
            }.collect { resultList ->
                Log.d("TAG","resultList")
                val userAchievementsList = mutableListOf<UserAchievement>()
                resultList.achievementList!!.forEach { achievement ->
                    val userAchievement = achievement.convertToUserAchievement(Constants.TEST_USER)
                    achievement.registered?.let {
                        userAchievement.registrationAchievementProgress()
                    }
                    achievement.testsToDone?.let { testsToDone ->
                        userAchievement.testsAchievementProgress(achievement, resultList.testList)
                    }
                    achievement.modelsToSave?.let { modelsToSave ->
                        userAchievement.modelsAchievementProgress(
                            achievement,
                            resultList.modelsList
                        )
                    }
                    achievement.lecturesToRead?.let { lecturesToRead ->
                        userAchievement.readenLecturesAchievementProgress(
                            achievement,
                            resultList.progressList
                        )
                    }
                    userAchievementsList.add(userAchievement)
                }
                userAchievementsFlow.emit(userAchievementsList)
            }
        }

        launch {
            databaseRepository.getAchievements(Constants.TEST_USER).combine(userAchievementsFlow) { a, b ->
                Pair(a, b)
            }
                .collect {
                    val savedAchievements = it.first
                    val newAchievements = it.second
                    val unDoneAchievements = mutableListOf<UserAchievement>()
                    val doneAchievements = mutableListOf<UserAchievement>().apply {
                        addAll(savedAchievements)
                    }
                    newAchievements?.let {
                        it.forEach { newUserAchievement ->
                            newUserAchievement.apply {
                                if (!(this.id in savedAchievements.map { it.id }) && itemsDone >= itemsToDone) {
                                    doneAchievements.add(this)
                                } else {
                                    if (!(this.id in savedAchievements.map { it.id })) {
                                        unDoneAchievements.add(this)
                                    }
                                }
                            }
                        }
                    }
                    _doneAchievements.postValue(doneAchievements)
                    _unDoneAchievements.postValue(unDoneAchievements)
                }
        }
    }

    inner class ResultList(
        val testList: List<PassedUserTest>,
        val modelsList: List<Model3D>,
        val progressList: List<UserProgress>,
        val achievementList: List<Achievement>?
    )

    inner class LocalBinder : Binder() {
        fun getService(): AchievementService = this@AchievementService
    }
}