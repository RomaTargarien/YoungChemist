package com.example.youngchemist.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.youngchemist.model.user.PassedUserTest

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




