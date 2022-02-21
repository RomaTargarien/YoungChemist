package com.example.youngchemist.db.shared_pref

import com.example.youngchemist.ui.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow

interface UserPreferences {
    var loggedUsers: Set<String>
    var userState: Int
    val userStateFlow: MutableStateFlow<Resource<String>?>
    val bottomSheetState: MutableStateFlow<Float?>
}

