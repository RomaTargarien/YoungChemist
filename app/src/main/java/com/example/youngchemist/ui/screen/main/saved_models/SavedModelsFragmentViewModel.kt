package com.example.youngchemist.ui.screen.main.saved_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.ui.util.Resource
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedModelsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val databaseRepository: DatabaseRepository
): ViewModel() {

    private val _models3DState: MutableLiveData<Pair<List<Model3D>,Query>> = MutableLiveData()
    private val currentSearchingFlow: MutableStateFlow<String> = MutableStateFlow("%")
    val model3DState: LiveData<Pair<List<Model3D>,Query>> = _models3DState

    private var job: Job? = null
    private var searchingTextJob: Job? = null

    init {
        val model3D = Model3D("76V1UE5VssV0W8mXenibeUpvQxm1","1","dsa","1","22.09.1999")
        val model3D2 = Model3D("76V1UE5VssV0W8mXenibeUpvQxm1","2","dsa","2","22.09.1999")
        val model3D3 = Model3D("76V1UE5VssV0W8mXenibeUpvQxm1","3","dsa","3","22.09.1999")
        val model3D4 = Model3D("76V1UE5VssV0W8mXenibeUpvQxm1","4","dsa","4","22.09.1999")
        val model3D5 = Model3D("76V1UE5VssV0W8mXenibeUpvQxm1","5","dsa","5","22.09.1999")
        val modelsList = mutableListOf(model3D,model3D2,model3D3,model3D4,model3D5)
        viewModelScope.launch {
            for (item in modelsList) {
                databaseRepository.save3DModel(item)
            }
        }
        viewModelScope.launch {
            currentSearchingFlow.collect { title ->
                Log.d("TAG",title)
                launch {
                    databaseRepository.getAllModelsFlow("76V1UE5VssV0W8mXenibeUpvQxm1",title).collect {
                        if (title.equals("%") || title.equals("")) {
                            _models3DState.postValue(Pair(it,Query.All()))
                        } else {
                            _models3DState.postValue(Pair(it,Query.Searching()))
                        }
                    }
                }
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
        viewModelScope.launch(Dispatchers.Default) {
            Log.d("TAG","search -$s")
            delay(300)
            currentSearchingFlow.emit("$s%")
        }
    }

    fun cancel() {
        job?.cancel()
    }

    fun start() {
        job?.start()
    }
}

sealed class Query {
    class Searching : Query()
    class All : Query()
}