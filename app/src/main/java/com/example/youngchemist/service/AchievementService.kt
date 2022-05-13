package com.example.youngchemist.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.youngchemist.model.Achievement
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

@AndroidEntryPoint
class AchievementService : Service(), CoroutineScope {

    private val binder = LocalBinder()

    @Inject
    lateinit var fireStoreRepository: FireStoreRepository

    @Inject
    lateinit var databaseRepository: DatabaseRepository

    @Inject
    lateinit var currentUser: FirebaseUser

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

    private val _doneAchievementsPercentage: MutableLiveData<ResourceNetwork<Int>> =
        MutableLiveData()
    val doneAchievementsPercentage: LiveData<ResourceNetwork<Int>> = _doneAchievementsPercentage

    override fun onBind(intent: Intent): IBinder {
        observeAchivementsProgress()
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        getAchievements()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        coroutineJob.cancel()
        super.onDestroy()
    }

    private fun getAchievements() {
        launch {
            when (val result = fireStoreRepository.getAllAchivements()) {
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
            val passedUserTests = databaseRepository.getAllPassedUserTests(currentUser.uid)
            val userModels = databaseRepository.getAllModelsFlow(currentUser.uid)
            val userProgress = databaseRepository.getProgress(currentUser.uid)
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
                val userAchievementsList = mutableListOf<UserAchievement>()
                resultList.achievementList!!.forEach { achievement ->
                    val userAchievement = achievement.convertToUserAchievement(currentUser.uid)
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
            databaseRepository.getAchievements(currentUser.uid)
                .combine(userAchievementsFlow) { a, b ->
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
                    _doneAchievementsPercentage.postValue(
                        ResourceNetwork.Success(
                            currentAchievementsDonePercentage(
                                unDoneAchievements.size + doneAchievements.size,
                                doneAchievements.size
                            )
                        )
                    )
                    _doneAchievements.postValue(doneAchievements)
                    _unDoneAchievements.postValue(unDoneAchievements)
                }
        }
    }

    private fun currentAchievementsDonePercentage(all: Int, done: Int): Int {
        var number = 0
        if (done != 0) {
            number = ((done.toDouble() / all.toDouble()) * 100).roundToInt()
        }
        return number
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