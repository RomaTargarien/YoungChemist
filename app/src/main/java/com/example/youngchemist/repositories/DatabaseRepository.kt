package com.example.youngchemist.repositories

import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Subject
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserProgress
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {

    suspend fun getAllSubjects(): List<Subject>

    suspend fun insertNewSubjects(subjects: List<Subject>)

    suspend fun deleteAllSubjects()

    fun getAllLectures(collectionId: String): Deferred<List<Lecture>>

    suspend fun insertNewLectures(lectures: List<Lecture>)

    suspend fun deleteAlllectures()

    suspend fun savePassedUserTest(passedUserTest: PassedUserTest)

    suspend fun save3DModel(model3D: Model3D)

    suspend fun getAll3DModels(userId: String = "%"): List<Model3D>

    suspend fun deleteModel(model3D: Model3D)

    suspend fun getModel(currentUserId: String, modelId: String): Model3D?

    suspend fun saveProgress(userProgress: UserProgress)

    suspend fun getProgress(userId: String = "%"): Flow<List<UserProgress>>

    suspend fun getAllPassedUserTests(userId: String): Flow<List<PassedUserTest>>

    suspend fun getPassedUserTest(userId: String, testId: String): PassedUserTest?

}