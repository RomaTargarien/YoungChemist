package com.example.youngchemist.repositories

import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork

interface AuthRepository {

    suspend fun register(login: String, surname: String, password: String): ResourceNetwork<String>

    suspend fun login(login: String, password: String): ResourceNetwork<String>

    suspend fun restorePassword(login: String): ResourceNetwork<String>

}