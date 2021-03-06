package com.chemist.youngchemist.ui.screen.main.qr.qr_code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chemist.youngchemist.model.user.Model3D
import com.chemist.youngchemist.repositories.DatabaseRepository
import com.chemist.youngchemist.repositories.FireStoreRepository
import com.chemist.youngchemist.ui.screen.Screens
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class QrCodeFragmentViewModel @Inject constructor(
    private val router: Router,
    private val fireStoreRepository: FireStoreRepository,
    private val databaseRepository: DatabaseRepository,
    private val currentUser: FirebaseUser
) : ViewModel() {

    private val _model3DState: MutableLiveData<ResourceNetwork<Model3D>> = MutableLiveData()
    val model3DState: LiveData<ResourceNetwork<Model3D>> = _model3DState

    private val model3DFlow: MutableStateFlow<Model3D?> = MutableStateFlow(null)
    val saveFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _wasSaved: MutableLiveData<Boolean> = MutableLiveData()
    val wasSaved: LiveData<Boolean> = _wasSaved

    var lastSelectedItemPosition: Int = 0

    init {
        observeSavingState()
    }

    private fun observeSavingState() {
        viewModelScope.launch {
            model3DFlow
                .filterNotNull()
                .combine(saveFlow) { model, save ->
                    Pair(model, save)
                }.collect { pair ->
                    if (pair.second) {
                        pair.first.userId = currentUser.uid
                        pair.first.addingDate = getCurrentTime()
                        databaseRepository.save3DModel(pair.first)
                        _wasSaved.postValue(true)
                    }
                }
        }
    }

    fun exit() {
        router.newRootScreen(Screens.mainScreen(lastSelectedItemPosition))
    }

    fun tryAgain() {
        router.replaceScreen(Screens.scanScreen(lastSelectedItemPosition))
    }

    fun get3DModel(modelId: String) {
        viewModelScope.launch {
            _model3DState.postValue(ResourceNetwork.Loading())
            val model = databaseRepository.getModel(currentUser.uid, modelId)
            if (model != null) {
                _model3DState.postValue(ResourceNetwork.Success(model))
                _wasSaved.postValue(true)
            } else {
                val result = fireStoreRepository.get3DModel(modelId)
                if (result is ResourceNetwork.Success) {
                    result.data?.let {
                        model3DFlow.emit(it)
                    }
                }
                _model3DState.postValue(result)
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            saveFlow.emit(true)
        }
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.US)
        val date = Calendar.getInstance().time
        return dateFormat.format(date)
    }
}