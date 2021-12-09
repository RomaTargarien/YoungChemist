package com.example.youngchemist.repositories.impl

import com.example.youngchemist.db.dao.LectureDao
import com.example.youngchemist.db.dao.Model3DDao
import com.example.youngchemist.db.dao.SubjectDao
import com.example.youngchemist.db.dao.TestDao
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Subject
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
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
    private val model3DDao: Model3DDao
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

    override fun getAll3DModels(userId: String): Flow<List<Model3D>> {
        return model3DDao.getAllModels(userId)
    }

    override suspend fun deleteModel(model3D: Model3D) = withContext(Dispatchers.IO) {
        model3DDao.deleteModel(model3D)
    }
}