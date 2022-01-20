package com.example.youngchemist.ui.screen.main.saved_models

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.repositories.DatabaseRepository
import com.example.youngchemist.ui.util.evaluateTime
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SavedModelsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _models3DState: MutableLiveData<Pair<List<Model3D>, Query>> = MutableLiveData()
    private val currentSearchingFlow: MutableStateFlow<String> = MutableStateFlow("")
    val model3DState: LiveData<Pair<List<Model3D>, Query>> = _models3DState

    private var job: Job? = null
    private var searchingTextJob: Job? = null

    init {
        val modelsList = mutableListOf<Model3D>()
        for (i in 0..5) {
            val uid = UUID.randomUUID().toString()
            val modelTitle = Random().nextInt(100)
            val model3D = Model3D(
                "76V1UE5VssV0W8mXenibeUpvQxm1",
                uid,
                "44",
                modelTitle.toString(),
                "22.09.1999"
            )
            modelsList.add(model3D)
        }
        viewModelScope.launch {
            for (item in modelsList) {
                databaseRepository.save3DModel(item)
            }
        }
        viewModelScope.launch {
            combine(
                currentSearchingFlow,
                databaseRepository.getAllModelsFlow("76V1UE5VssV0W8mXenibeUpvQxm1")
            ) { title, list ->
                Log.d("TAG", title)
                val list2 = list.filter {
                    it.modelTitle.startsWith(title)
                }
                if (title.isEmpty()) {
                    Pair(list2, Query.All())
                } else {
                    Pair(list2, Query.Searching())
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
            Log.d("TAG", "search -$s")
            delay(300)
            currentSearchingFlow.emit(s.toString())
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