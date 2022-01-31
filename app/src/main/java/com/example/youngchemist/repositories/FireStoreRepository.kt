package com.example.youngchemist.repositories

import com.example.youngchemist.model.*
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress
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

    suspend fun updateUserInfo(user: User): ResourceNetwork<String>

    suspend fun saveLecture(lecture: Lecture): ResourceNetwork<String>

    suspend fun save3DModels(userId: String, models3D: List<Model3D>): ResourceNetwork<String>

    suspend fun saveUserProgress(
        userId: String,
        userProgress: List<UserProgress>
    ): ResourceNetwork<String>

    suspend fun saveUserDoneAchievements(
        userId: String,
        doneAchievements: List<UserAchievement>
    ): ResourceNetwork<String>

    suspend fun getUserModels3D(userId: String): ResourceNetwork<ArrayList<Model3D>>

    suspend fun saveAchivement(achievement: Achievement): ResourceNetwork<String>

    suspend fun getAllAchivements(): ResourceNetwork<List<Achievement>>
}