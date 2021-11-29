package com.example.youngchemist.repositories

import android.net.Uri
import com.example.youngchemist.model.*
import com.example.youngchemist.ui.util.ResourceNetwork

interface FireStoreRepository {

    suspend fun getAllSubjects(): ResourceNetwork<List<Subject>>

    suspend fun getAllLectures(subjectTitle: String): ResourceNetwork<List<Lecture>>

    suspend fun get3DModel(name: String): ResourceNetwork<Uri>

    suspend fun saveTest(test: Test)

    suspend fun retriveTest(uid: String): ResourceNetwork<Test>

    suspend fun saveTest(userUid: String,passedUserTest: PassedUserTest): ResourceNetwork<String>

}