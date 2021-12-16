package com.example.youngchemist.db.shared_pref

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.UserState
import kotlinx.coroutines.flow.MutableStateFlow

interface UserPreferences {
    var loggedUsers: Set<String>
    var loggedUserState: MutableLiveData<Set<String>>
    var userState: Int
    val userStateFlow: MutableStateFlow<Resource<String>?>
}

