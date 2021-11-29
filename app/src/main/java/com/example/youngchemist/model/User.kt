package com.example.youngchemist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    val uid: String = "",
    val surname: String = "",
    var passedUserTests: ArrayList<PassedUserTest> = arrayListOf(),
)

@Entity(tableName = "unsavedTests")
data class PassedUserTest(
    @PrimaryKey
    var testUid: String = "",
    var userUid: String = "",
    var mark: Double = 0.0,
    var answers: ArrayList<AnswerUser> = arrayListOf()
)

data class AnswerUser(
    var questionPosition: Int = -1,
    var itIsRight: Boolean = false,
)