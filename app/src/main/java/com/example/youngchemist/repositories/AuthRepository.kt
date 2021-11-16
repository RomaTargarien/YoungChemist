package com.example.youngchemist.repositories

import com.example.youngchemist.model.AuthResults
import com.example.youngchemist.ui.util.Resource
import com.example.youngchemist.ui.util.ResourceNetwork

interface AuthRepository {

    suspend fun register(authResults: AuthResults): ResourceNetwork<String>

    suspend fun login(login: String, password: String): ResourceNetwork<String>

    suspend fun restorePassword(login: String): ResourceNetwork<String>

}