package com.example.youngchemist.repositories

import androidx.room.PrimaryKey
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Subject
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {

    //Subjects database
    suspend fun getSubjects(): Flow<List<Subject>>

    suspend fun getSubjectByPrimaryKey(primaryKey: Int): Subject

    suspend fun saveSubject(subject: Subject)

    //Lectures database
    suspend fun getLectures(collectionId: String): Flow<List<Lecture>>

    suspend fun saveLecture(lecture: Lecture)

    //PassedUserTests database
    suspend fun savePassedUserTest(passedUserTest: PassedUserTest)

    suspend fun getAllPassedUserTests(userId: String): Flow<List<PassedUserTest>>

    suspend fun getPassedUserTest(userId: String, testId: String): PassedUserTest?

    //3DModels database
    suspend fun save3DModel(model3D: Model3D)

    suspend fun getAll3DModels(userId: String = "%"): List<Model3D>

    suspend fun deleteModel(model3D: Model3D)

    suspend fun getModel(currentUserId: String, modelId: String): Model3D?

    suspend fun getAllModelsFlow(currentUserId: String): Flow<List<Model3D>>

    //UserProgress database
    suspend fun saveProgress(userProgress: UserProgress)

    suspend fun getProgress(userId: String = "%"): Flow<List<UserProgress>>

    //Achivemenets database
    suspend fun saveAchievement(userAchievement: UserAchievement)

    suspend fun getAchievements(userId: String): Flow<List<UserAchievement>>

    suspend fun getAchievementByPrimaryKey(primaryKey: Int): UserAchievement


}