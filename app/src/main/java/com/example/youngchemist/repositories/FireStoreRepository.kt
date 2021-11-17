package com.example.youngchemist.repositories

import com.example.youngchemist.model.Content
import com.example.youngchemist.model.Lection
import com.example.youngchemist.model.Subject
import com.example.youngchemist.ui.util.ResourceNetwork

interface FireStoreRepository {

    suspend fun getAllSubjects(): ResourceNetwork<List<Subject>>

    suspend fun getAllLectures(subjectTitle: String): ResourceNetwork<List<Lection>>

    suspend fun getLecture(subjectTitle: String,lectureTitle: String): ResourceNetwork<Content>

}