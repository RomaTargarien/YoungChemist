package com.example.youngchemist.ui.screen.main.saved_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.repositories.DatabaseRepository
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedModelsFragmentViewModel @Inject constructor(
    private val router: Router,
    private val databaseRepository: DatabaseRepository
): ViewModel() {

    private val _models3DState: MutableLiveData<List<Model3D>> = MutableLiveData()
    val model3DState: LiveData<List<Model3D>> = _models3DState

    init {
        getAllModels3D()
    }

    fun getAllModels3D() {
        viewModelScope.launch {
            val models = databaseRepository.getAll3DModels("dJuRGOc06xhllmscaAEqQoHC9Ir2")
            _models3DState.postValue(models)
        }
    }
    fun deleteModel3D(model3D: Model3D) {
        viewModelScope.launch {
            databaseRepository.deleteModel(model3D)
        }
    }
}