package com.example.youngchemist.repositories

import android.net.Uri
import com.example.youngchemist.model.Content
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Subject
import com.example.youngchemist.ui.util.ResourceNetwork

interface FireStoreRepository {

    suspend fun getAllSubjects(): ResourceNetwork<List<Subject>>

    suspend fun getAllLectures(subjectTitle: String): ResourceNetwork<List<Lecture>>

    suspend fun get3DModel(name: String): ResourceNetwork<Uri>

}