package com.example.youngchemist.repositories

import android.net.Uri
import com.example.youngchemist.model.*
import com.example.youngchemist.ui.util.ResourceNetwork
import kotlinx.coroutines.Deferred

interface FireStoreRepository {

    suspend fun getAllSubjects(): ResourceNetwork<List<Subject>>

    suspend fun getAllLectures(collectionId: String): ResourceNetwork<List<Lecture>>

    suspend fun get3DModel(name: String): ResourceNetwork<Uri>

    suspend fun saveTest(test: Test)

    suspend fun retriveTest(uid: String): ResourceNetwork<Test>

    suspend fun saveTest(userUid: String,passedUserTest: PassedUserTest): ResourceNetwork<String>

    suspend fun getUser(userUid: String): Deferred<ResourceNetwork<User>>

    suspend fun updateReadenLectures(lectureId: String): ResourceNetwork<String>
}