package com.example.youngchemist.repositories

import androidx.lifecycle.LiveData
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Page
import com.example.youngchemist.model.PassedUserTest
import com.example.youngchemist.model.Subject

interface DatabaseRepository {

    suspend fun getAllSubjects(): List<Subject>

    suspend fun insertNewSubjects(subjects: List<Subject>)

    suspend fun deleteAllSubjects()

    suspend fun getAllLectures(subjectTitle: String): List<Lecture>

    suspend fun insertNewLectures(lectures: List<Lecture>)

    suspend fun deleteAlllectures()

    suspend fun getAllPages(lectureTitle: String,subjectTitle: String): List<Lecture>

    suspend fun savePassedUserTest(passedUserTest: PassedUserTest)

}