package com.example.youngchemist.repositories

import com.example.youngchemist.model.Subject
import com.example.youngchemist.ui.util.ResourceNetwork

interface FireStoreRepository {

    suspend fun getAllSubjects(): ResourceNetwork<List<Subject>>

}