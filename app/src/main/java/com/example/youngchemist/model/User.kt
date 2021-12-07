package com.example.youngchemist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    val uid: String = "",
    val surname: String = "",
    var passedUserTests: ArrayList<PassedUserTest> = arrayListOf(),
    var readenLectures: ArrayList<String> = arrayListOf()
) {
    fun passedTestsId(): List<String> {
        val listTestsId = mutableListOf<String>()
        for (item in passedUserTests) {
            listTestsId.add(item.testUid)
        }
        return listTestsId
    }
    fun passedTestMark(testId: String): Double {
        var mark = 0.0
        if (testId in passedTestsId()) {
            passedUserTests.find { passedTest ->
               testId.equals(passedTest.testUid)
            }?.let { mark = it.mark }
        }
        return mark
    }
}

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

@Entity(tableName = "saved3DModels",primaryKeys = ["userId","modelId"])
data class Saved3DModel(
    var userId: String = "",
    var modelId: String = "",
    var modelUri: String = "",
    var modelTitle: String = ""
)