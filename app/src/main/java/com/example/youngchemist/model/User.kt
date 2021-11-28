package com.example.youngchemist.model

data class User(
    val uid: String = "",
    val surname: String = "",
    var passedUserTests: ArrayList<PassedUserTest> = arrayListOf(),
)

data class PassedUserTest(
    var testUid: String ="",
    var mark: Double = 0.0,
    var answers: ArrayList<AnswerUser> = arrayListOf()
)

data class AnswerUser(
    var questionPosition: Int = -1,
    var itIsRight: Boolean = false,
)