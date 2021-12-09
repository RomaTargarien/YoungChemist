package com.example.youngchemist.repositories

import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Subject
import com.example.youngchemist.model.Test
import com.example.youngchemist.model.User
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.ui.util.ResourceNetwork
import kotlinx.coroutines.Deferred

interface FireStoreRepository {

    suspend fun getAllSubjects(): ResourceNetwork<List<Subject>>

    suspend fun getAllLectures(collectionId: String): ResourceNetwork<List<Lecture>>

    suspend fun get3DModel(modelId: String): ResourceNetwork<Model3D>

    suspend fun saveTest(test: Test)

    suspend fun retriveTest(uid: String): ResourceNetwork<Test>

    suspend fun saveTest(userUid: String, passedUserTest: PassedUserTest): ResourceNetwork<String>

    suspend fun getUser(userUid: String): Deferred<ResourceNetwork<User>>

    suspend fun updateReadenLectures(lectureId: String): ResourceNetwork<String>

    suspend fun saveLecture(lecture: Lecture): ResourceNetwork<String>
}