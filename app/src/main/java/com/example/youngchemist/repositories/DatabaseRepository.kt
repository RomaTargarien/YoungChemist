package com.example.youngchemist.repositories

import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.PassedUserTest
import com.example.youngchemist.model.Subject

interface DatabaseRepository {

    suspend fun getAllSubjects(): List<Subject>

    suspend fun insertNewSubjects(subjects: List<Subject>)

    suspend fun deleteAllSubjects()

    suspend fun getAllLectures(collectionId: String): List<Lecture>

    suspend fun insertNewLectures(lectures: List<Lecture>)

    suspend fun deleteAlllectures()

    suspend fun savePassedUserTest(passedUserTest: PassedUserTest)

}