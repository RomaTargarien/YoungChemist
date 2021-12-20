package com.example.youngchemist.db.shared_pref

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.UserState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class UserPreferenceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferences {

    private val PREFS_NAME = "prefs.db"
    val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val KEY_USER_ID = "key.user"


    override var loggedUsers: Set<String> = sharedPref.let {
        val set = mutableSetOf<String>()
        sharedPref.getStringSet(KEY_USER_ID, null)?.let {
            for (item in it) {
                set.add(item)
            }
        }
        set
    }
        set(value) {
            val edit = sharedPref.edit()
            edit.putStringSet(KEY_USER_ID, value)
            edit.apply()
            field = value
        }

    override var userState: Int = -1
        get() = field
        set(value) {
            field = value
        }

    override val userStateFlow: MutableStateFlow<Resource<String>?> = MutableStateFlow(null)

    override val bottomSheetState: MutableStateFlow<Float?> = MutableStateFlow(null)
}