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

    val subjectSearchText: MutableStateFlow<String> = MutableStateFlow("")


    init {
        getAllSubjects()
        getUserName()
        viewModelScope.launch {
            userPreferences.userStateFlow.filterNotNull().collect {
                _userState.postValue(it)
            }
        }
    }

    fun initSubjectSearch() {
        viewModelScope.launch {
            subjectSearchText.debounce(300).collect {

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
                }
        }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            when (val result = fireStoreRepository.getAllSubjects()) {
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

    private fun getUserName() {
        viewModelScope.launch {
            _userName.postValue(ResourceNetwork.Loading())
            val result = fireStoreRepository.getUser(currentUser.uid).await()
            if (result is ResourceNetwork.Success) {
                result.data?.let { _userName.postValue(ResourceNetwork.Success(it.name)) }
            }
        }
    }
}