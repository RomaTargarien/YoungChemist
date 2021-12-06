package com.example.youngchemist.repositories

import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.PassedUserTest
import com.example.youngchemist.model.Subject
import kotlinx.coroutines.Deferred

interface DatabaseRepository {

    suspend fun getAllSubjects(): List<Subject>

    suspend fun insertNewSubjects(subjects: List<Subject>)

    suspend fun deleteAllSubjects()

    fun getAllLectures(collectionId: String): Deferred<List<Lecture>>

    suspend fun insertNewLectures(lectures: List<Lecture>)

    suspend fun deleteAlllectures()

    suspend fun savePassedUserTest(passedUserTest: PassedUserTest)

}