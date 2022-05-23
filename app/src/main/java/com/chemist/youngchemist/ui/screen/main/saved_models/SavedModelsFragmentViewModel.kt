package com.chemist.youngchemist.ui.screen.main.saved_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.model.user.Model3D
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedModelsFragmentViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val currentUser: FirebaseUser
) : ViewModel() {

    private val _models3DState: MutableLiveData<Pair<List<Model3D>, Query>> = MutableLiveData()
    private val currentSearchingFlow: MutableStateFlow<String> = MutableStateFlow("")
    val model3DState: LiveData<Pair<List<Model3D>, Query>> = _models3DState

    private var searchingTextJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                currentSearchingFlow,
                databaseRepository.getAllModelsFlow(currentUser.uid)
            ) { title, list ->
                val list2 = list.filter {
                    it.modelTitle.lowercase().startsWith(title)
                }
                if (title.isEmpty()) {
                    Pair(list2, Query.All)
                } else {
                    Pair(list2, Query.Searching)
                }
            }.collect {
                _models3DState.postValue(it)
            }
        }
    }

    fun deleteModel3D(model3D: Model3D) {
        viewModelScope.launch {
            databaseRepository.deleteModel(model3D)
        }
    }

    fun undoDelete(model3D: Model3D) {
        viewModelScope.launch {
            databaseRepository.save3DModel(model3D)
        }
    }

    fun onModelNameSearchTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        searchingTextJob?.cancel()
        searchingTextJob = viewModelScope.launch(Dispatchers.Default) {
            delay(300)
            currentSearchingFlow.emit(s.toString())
        }
    }
}

sealed class Query {
    object Searching : Query()
    object All : Query()
}