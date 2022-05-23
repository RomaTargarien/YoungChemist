package com.chemist.youngchemist.repositories

import com.chemist.youngchemist.model.AuthResults
import com.chemist.youngchemist.ui.util.ResourceNetwork

interface AuthRepository {

    suspend fun register(login: String,password: String,name: String,surname: String): ResourceNetwork<String>

    suspend fun login(login: String, password: String): ResourceNetwork<String>

    suspend fun restorePassword(login: String): ResourceNetwork<String>

    suspend fun reauthenticate(password: String): ResourceNetwork<String>

    suspend fun updateEmail(email: String): ResourceNetwork<String>

    suspend fun updatePassword(password: String): ResourceNetwork<String>

    suspend fun logOut(): ResourceNetwork<String>
}