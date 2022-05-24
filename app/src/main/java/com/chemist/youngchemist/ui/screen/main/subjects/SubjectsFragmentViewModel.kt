package com.chemist.youngchemist.ui.screen.main.subjects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.db.shared_pref.UserPreferences
import com.chemist.youngchemist.model.Subject
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.chemist.youngchemist.repositories.FireStoreRepository
import com.chemist.youngchemist.ui.screen.Screens
import com.chemist.youngchemist.ui.util.Resource
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class SubjectsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository,
    private val userPreferences: UserPreferences,
    private val currentUser: FirebaseUser
): ViewModel() {

    private val _subjectsState: MutableLiveData<ResourceNetwork<List<Subject>>> = MutableLiveData()
    val subjectsState: LiveData<ResourceNetwork<List<Subject>>> = _subjectsState

    private val _userState: MutableLiveData<Resource<String>> = MutableLiveData()
    val userState: LiveData<Resource<String>> = _userState

    private val _userName: MutableLiveData<ResourceNetwork<String>> = MutableLiveData()
    val userName: LiveData<ResourceNetwork<String>> = _userName

    private val subjectIds: MutableSet<String> = mutableSetOf()
    val subjectSearchText: MutableStateFlow<String> = MutableStateFlow("")

    private val _numberOfSubjectsDownloaded: MutableSharedFlow<Int?> = MutableSharedFlow()
    val numberOfSubjectsDownloaded: SharedFlow<Int?> = _numberOfSubjectsDownloaded

    init {
        getUserName()
        initSubjectsFlow()
        initUserStateFlow()
    }

    fun navigateToLecturesListScreen(subject: Subject) {
        router.navigateTo(Screens.lecturesListScreen(subject))
    }

    fun tryAgain() {
        loadSubjects()
    }

    fun loadSubjects() {
        viewModelScope.launch {
            _subjectsState.postValue(ResourceNetwork.Loading())
            when (val result = fireStoreRepository.getAllSubjects()) {
                is ResourceNetwork.Success -> {
                    var newSubjectsNumber = 0
                    result.data?.forEach {
                        if (it.subjectId !in subjectIds) {
                            newSubjectsNumber++
                            databaseRepository.saveSubject(it)
                            subjectIds.add(it.subjectId)
                        }
                    }
                    _numberOfSubjectsDownloaded.emit(newSubjectsNumber)
                }
                is ResourceNetwork.Error -> {
                    _numberOfSubjectsDownloaded.emit(null)
                    _subjectsState.postValue(ResourceNetwork.Error(result.message))
                }
            }
        }
    }

    private fun getUserName() {
        viewModelScope.launch {
            _userName.postValue(ResourceNetwork.Loading())
            val result = fireStoreRepository.getUser(currentUser.uid).await()
            if (result is ResourceNetwork.Success) {
                result.data?.let { _userName.postValue(ResourceNetwork.Success(it.name)) }
            }
        }
    }

    private fun initSubjectsFlow() {
        viewModelScope.launch {
            combine(
                subjectSearchText,
                databaseRepository.getSubjects()
            ) { title, list ->
                list.forEach {
                    subjectIds.add(it.subjectId)
                }
                if (title.isEmpty() && list.isEmpty()) {
                    loadSubjects()
                    ResourceNetwork.Loading()
                } else {
                    ResourceNetwork.Success(list.filter { it.title.lowercase().startsWith(title) })
                }
            }.collect {
                _subjectsState.postValue(it)
            }
        }
    }

    private fun initUserStateFlow() {
        viewModelScope.launch {
            userPreferences.userStateFlow.filterNotNull().collect {
                _userState.postValue(it)
            }
        }
    }
}