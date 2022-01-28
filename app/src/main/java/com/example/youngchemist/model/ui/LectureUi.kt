package com.example.youngchemist.model.ui

import android.os.Parcelable
import com.example.youngchemist.model.Test
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.ui.util.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LectureUi(
    var lectureId: String = "",
    var collectionId: String = "",
    var lectureTitle: String = "",
    var lectureDescription: String ="",
    var lectureKeyWord: String = "",
    var data: ArrayList<String> = arrayListOf(),
    var isTestEnabled: Boolean = false,
    var mark: Double = 0.0,
    var test: Test? = null,
    var userProgress: UserProgress? = null
): Parcelable {

    fun addUserPassedTests(userPassedTests: List<PassedUserTest>) {
        test?.let { test ->
            isTestEnabled = true
            val passedUserTest = userPassedTests.find { passedUserTest ->
                test.testId.equals(passedUserTest.testUid)
            }
            passedUserTest?.let {
                isTestEnabled = false
                mark = it.mark
            }
        }
    }

    fun addUserProgress(userProgressList: List<UserProgress>):Boolean {
        userProgressList.find { userProgress ->
            userProgress.lectureId == lectureId
        }.also {
            if (it == null) {
                return false
            } else {
                userProgress = it
                return true
            }
        }
    }
}