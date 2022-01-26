package com.example.youngchemist.repositories.impl

import android.util.Log
import com.example.youngchemist.db.dao.*
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Subject
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.DatabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val subjectsDao: SubjectDao,
    private val lecturesDao: LectureDao,
    private val testDao: TestDao,
    private val model3DDao: Model3DDao,
    private val userProgressDao: UserProgressDao,
    private val achievementsDao: AchievementsDao
) : DatabaseRepository {

    override suspend fun getAllSubjects(): List<Subject> {
        return withContext(Dispatchers.IO) {
            subjectsDao.getAllSubjects()
        }
    }

    override suspend fun insertNewSubjects(subjects: List<Subject>) {
        withContext(Dispatchers.IO) {
            subjectsDao.insertNewSubjects(subjects)
        }
    }

    override suspend fun deleteAllSubjects() {
        withContext(Dispatchers.IO) {
            subjectsDao.delete()
        }
    }

    override fun getAllLectures(collectionId: String) = CoroutineScope(Dispatchers.IO).async {
        lecturesDao.getAllLectures(collectionId)
    }


    override suspend fun insertNewLectures(lectures: List<Lecture>) {
        withContext(Dispatchers.IO) {
            lecturesDao.insertNewLectures(lectures)
        }
    }

    override suspend fun deleteAlllectures() {
        withContext(Dispatchers.IO) {

        }
    }

    override suspend fun savePassedUserTest(passedUserTest: PassedUserTest) {
        withContext(Dispatchers.IO) {
            testDao.savePassedUserTest(passedUserTest)
        }
    }

    override suspend fun save3DModel(model3D: Model3D) {
        withContext(Dispatchers.IO) {
            model3DDao.saveModel3D(model3D)
        }
    }

    override suspend fun getAll3DModels(userId: String): List<Model3D> {
        return withContext(Dispatchers.IO) {
            model3DDao.getAllModels(userId)
        }

    }

    override suspend fun deleteModel(model3D: Model3D) = withContext(Dispatchers.IO) {
        model3DDao.deleteModel(model3D)
    }

    override suspend fun getModel(currentUserId: String, modelId: String) =
        withContext(Dispatchers.IO) {
            val models = model3DDao.getModel(currentUserId, modelId)
            if (models.isNotEmpty()) {
                models[0]
            } else null
        }

    override suspend fun saveProgress(userProgress: UserProgress) {
        withContext(Dispatchers.IO) {
            userProgressDao.writeProgress(userProgress)
        }
    }

    override suspend fun getProgress(userId: String) = withContext(Dispatchers.IO) {
        userProgressDao.getProgress(userId)
    }

    override suspend fun getAllPassedUserTests(userId: String) = withContext(Dispatchers.IO) {
        testDao.getAllTests(userId)
    }

    override suspend fun getPassedUserTest(userId: String, testId: String) =
        withContext(Dispatchers.IO) {
            val tests = testDao.getTest(userId, testId)
            if (tests.isNotEmpty()) {
                tests[0]
            } else null
        }

    override suspend fun getAllModelsFlow(currentUserId: String) = withContext(Dispatchers.IO) {
        model3DDao.getAllModelsFlow(currentUserId)
    }

    override suspend fun getAchievements(userId: String) = withContext(Dispatchers.IO) {
        achievementsDao.getAchievements(userId)
    }

    override suspend fun saveAchievement(userAchievement: UserAchievement) {
        withContext(Dispatchers.IO) {
            achievementsDao.insertAchievement(userAchievement)
        }
    }

    override suspend fun getAchievementByPrimaryKey(primaryKey: Int) = withContext(Dispatchers.IO) {
        achievementsDao.getAchievementByPrimaryKey(primaryKey)
    }
}