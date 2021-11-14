package com.example.youngchemist.ui.screen.auth.password_restore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youngchemist.ui.base.validation.ValidationImpl.LoginValidation
import com.example.youngchemist.ui.util.Resource
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestorePasswordFragmentViewModel @Inject constructor(
    private val router: Router,
    private val loginValidation: LoginValidation
) : ViewModel() {

    var loginText = ""

    private var loginJob: Job? = null
    private val stateLogin = MutableStateFlow<Resource<String>>(Resource.Error(""))

    private val _errorLoginMessageBehavior: MutableLiveData<Pair<String?, Boolean>> =
        MutableLiveData<Pair<String?, Boolean>>()
    val errorLoginMessageBehavior: LiveData<Pair<String?,Boolean>> = _errorLoginMessageBehavior

    private val _restorePasswordButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val restorePasswordButtonEnabled: LiveData<Boolean> = _restorePasswordButtonEnabled


    init {
        observe()
    }

    fun exit() {
        router.exit()
    }

    fun onLoginTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        loginText = s.toString()
        loginJob?.cancel()
        _restorePasswordButtonEnabled.postValue(false)
        _errorLoginMessageBehavior.postValue(Pair(null,false))
        loginJob = viewModelScope.launch(Dispatchers.Default) {
            flow {
                delay(1000)
                emit(loginValidation.validate(s.toString()))
            }.collect {
                stateLogin.emit(it)
            }
        }
    }

    private fun observe() {
        viewModelScope.launch {
            stateLogin.collect {
                if (it is Resource.Success) {
                    _restorePasswordButtonEnabled.postValue(true)
                } else {
                    _restorePasswordButtonEnabled.postValue(false)
                    _errorLoginMessageBehavior.postValue(Pair(it.message,true))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        loginJob?.cancel()
    }

}