package com.example.youngchemist.repositories.impl

import android.util.Log
import com.example.youngchemist.db.LectureDao
import com.example.youngchemist.db.SubjectDao
import com.example.youngchemist.db.TestDao
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.PassedUserTest
import com.example.youngchemist.model.Subject
import com.example.youngchemist.repositories.DatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val subjectsDao: SubjectDao,
    private val lecturesDao: LectureDao,
    private val testDao: TestDao
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

    override suspend fun getAllLectures(collectionId: String): List<Lecture> {
        return withContext(Dispatchers.IO) {
            lecturesDao.getAllLectures(collectionId)
        }
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
}