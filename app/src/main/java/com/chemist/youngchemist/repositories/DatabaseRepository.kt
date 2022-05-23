package com.chemist.youngchemist.repositories

import com.chemist.youngchemist.model.Lecture
import com.chemist.youngchemist.model.Subject
import com.chemist.youngchemist.model.user.Model3D
import com.chemist.youngchemist.model.user.PassedUserTest
import com.chemist.youngchemist.model.user.UserAchievement
import com.chemist.youngchemist.model.user.UserProgress
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {

    //Subjects database
    suspend fun getSubjects(): Flow<List<Subject>>

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

    suspend fun getAll3DModels(): List<Model3D>

    suspend fun deleteModel(model3D: Model3D)

    suspend fun getModel(currentUserId: String, modelId: String): Model3D?

    suspend fun getAllModelsFlow(currentUserId: String): Flow<List<Model3D>>

    //UserProgress database
    suspend fun saveProgress(userProgress: UserProgress)

    suspend fun getProgress(userId: String = "%"): Flow<List<UserProgress>>

    suspend fun getAllProgress(): List<UserProgress>

    //Achivemenets database
    suspend fun saveAchievement(userAchievement: UserAchievement)

    suspend fun getAchievements(userId: String): Flow<List<UserAchievement>>

    suspend fun getAchievementByPrimaryKey(primaryKey: Int): UserAchievement

    suspend fun getAllAchievements(): List<UserAchievement>


}