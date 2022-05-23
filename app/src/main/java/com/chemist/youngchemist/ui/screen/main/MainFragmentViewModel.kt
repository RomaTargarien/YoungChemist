package com.chemist.youngchemist.ui.screen.main

import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.chemist.youngchemist.db.shared_pref.UserPreferences
import com.chemist.youngchemist.model.Achievement
import com.chemist.youngchemist.model.user.Model3D
import com.chemist.youngchemist.model.user.PassedUserTest
import com.chemist.youngchemist.model.user.UserAchievement
import com.chemist.youngchemist.model.user.UserProgress
import com.chemist.youngchemist.repositories.AuthRepository
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.chemist.youngchemist.repositories.FireStoreRepository
import com.chemist.youngchemist.ui.base.workers.UserInfoDownloadingWorker
import com.chemist.youngchemist.ui.base.workers.UserInfoUploadingWorker
import com.chemist.youngchemist.ui.screen.Screens
import com.chemist.youngchemist.ui.util.Resource
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.chemist.youngchemist.ui.util.UserState
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.roundToInt

@FlowPreview
@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val userSharedPreferences: UserPreferences,
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository,
    private val authRepository: AuthRepository,
    private val currentUser: FirebaseUser
) : ViewModel() {

    private val _bottomSheetState: MutableLiveData<Float> = MutableLiveData()
    val bottomSheetState: LiveData<Float> = _bottomSheetState

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

    init {
        viewModelScope.launch {
            userSharedPreferences.bottomSheetState.emit(-1f)
        }
        when (userSharedPreferences.userState) {
            UserState.REGISTER -> {
                saveUser(currentUser.uid)
                viewModelScope.launch {
                    userSharedPreferences.userStateFlow.emit(Resource.Success())
                }
            }
            UserState.LOGIN -> {
                if (!(currentUser.uid in userSharedPreferences.loggedUsers)) {
                    downloadUserInfo(currentUser.uid)
                } else {
                    viewModelScope.launch {
                        userSharedPreferences.userStateFlow.emit(Resource.Success())
                    }
                }
            }
            UserState.LOGGED -> {
                viewModelScope.launch {
                    userSharedPreferences.userStateFlow.emit(Resource.Success())
                }
            }
        }

        viewModelScope.launch {
            userSharedPreferences.bottomSheetState.filterNotNull().collect {
                _bottomSheetState.postValue(it)
            }
        }
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<UserInfoUploadingWorker>(4, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(work)
    }

    fun exit() {
        router.exit()
    }

    fun navigateToScanFragemnt(lastSelectedItemPosition: Int) {
        router.navigateTo(Screens.scanScreen(lastSelectedItemPosition))
    }

    private fun downloadUserInfo(userId: String) {
        val data = Data.Builder().putString(KEY_USER_ID, userId).build()
        val workRequest =
            OneTimeWorkRequestBuilder<UserInfoDownloadingWorker>().setInputData(data).build()
        workManager.enqueue(workRequest)
        viewModelScope.launch {
            workManager.getWorkInfoByIdLiveData(workRequest.id).asFlow().collect {
                when (it.state.name) {
                    "RUNNING" -> {
                        userSharedPreferences.userStateFlow.emit(Resource.Loading())
                    }
                    "SUCCEEDED" -> {
                        userSharedPreferences.userStateFlow.emit(Resource.Success())
                    }
                    "FAILED" -> {
                        userSharedPreferences.userStateFlow.emit(Resource.Error("error"))
                    }
                }
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            val result = authRepository.logOut()
            if (result is ResourceNetwork.Success) {
                router.newRootScreen(Screens.authScreen())
            }
        }
    }

    private fun saveUser(userId: String) {
        val setOfUsers = mutableSetOf<String>()
        setOfUsers.addAll(userSharedPreferences.loggedUsers)
        setOfUsers.add(userId)
        userSharedPreferences.loggedUsers = setOfUsers
    }

    fun updateAchievements() {
        getAchievements()
        observeAchivementsProgress()
    }

    private fun getAchievements() {
        viewModelScope.launch {
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
        val user = FirebaseAuth.getInstance().currentUser!!
        viewModelScope.launch(Dispatchers.IO) {
            val passedUserTests = databaseRepository.getAllPassedUserTests(user.uid)
            val userModels = databaseRepository.getAllModelsFlow(user.uid)
            val userProgress = databaseRepository.getProgress(user.uid)
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
                    val userAchievement = achievement.convertToUserAchievement(user.uid)
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

        viewModelScope.launch {
            databaseRepository.getAchievements(user.uid)
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

    companion object {
        private const val KEY_USER_ID = "key_user"
    }
}