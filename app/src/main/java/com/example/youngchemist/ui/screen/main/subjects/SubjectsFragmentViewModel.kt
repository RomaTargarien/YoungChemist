package com.example.youngchemist.ui.screen.main.subjects

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.youngchemist.db.shared_pref.UserPreferences
import com.example.youngchemist.model.Subject
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.base.workers.ImageUrlDecoderWorker
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.Constants.SUBJECT_DATABASE
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val workManager: WorkManager,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository,
    private val userPreferences: UserPreferences
): ViewModel() {

    private val _subjectsState: MutableLiveData<ResourceNetwork<List<Subject>>> = MutableLiveData()
    val subjectsState: LiveData<ResourceNetwork<List<Subject>>> = _subjectsState

    private val _userState: MutableLiveData<Resource<String>> = MutableLiveData()
    val userState: LiveData<Resource<String>> = _userState

    init {
        getAllSubjects()
        viewModelScope.launch {
            userPreferences.userStateFlow.filterNotNull().collect {
                _userState.postValue(it)
            }
        }
    }

    fun navigateToLecturesListScreen(subject: Subject) {
        router.navigateTo(Screens.lecturesListScreen(subject))
    }

    fun tryAgain() {
        getAllSubjects()
    }

    private fun getAllSubjects() {
        viewModelScope.launch {
            _subjectsState.postValue(ResourceNetwork.Loading())
            databaseRepository.getSubjects()
                .onEach {
                    if (it.isEmpty()) {
                        loadSubjects()
                    }
                }
                .filterNot { it.isEmpty() }
                .collect {
                    _subjectsState.postValue(ResourceNetwork.Success(it))
                    decodeSubjectItemImageUrl(it)
                }
        }
    }

    private fun decodeSubjectItemImageUrl(subjectList: List<Subject>) {
        viewModelScope.launch(Dispatchers.Default) {
            subjectList.forEach {
                if (it.iconByteArray.isEmpty()) {
                    val data = Data.Builder()
                        .putInt(KEY_DATABASE_TYPE, SUBJECT_DATABASE)
                        .putInt(KEY_PRIMARY_KEY, it.subjectPrimaryKey)
                        .build()
                    val workRequest = OneTimeWorkRequestBuilder<ImageUrlDecoderWorker>()
                        .setInputData(data)
                        .build()
                    workManager.enqueue(workRequest)
                }
            }
        }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            val result = fireStoreRepository.getAllSubjects()
            when (result) {
                is ResourceNetwork.Success -> {
                    result.data?.forEach {
                        databaseRepository.saveSubject(it)
                    }
                }
                is ResourceNetwork.Error -> {
                    _subjectsState.postValue(ResourceNetwork.Error(result.message))
                }
            }
        }
    }

    companion object {
        private const val KEY_PRIMARY_KEY = "key.achievement.primary.key"
        private const val KEY_DATABASE_TYPE = "key.database.type"
    }
}