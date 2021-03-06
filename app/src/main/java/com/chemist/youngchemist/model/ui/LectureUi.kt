package com.chemist.youngchemist.model.ui

import android.os.Parcelable
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.model.user.PassedUserTest
import com.chemist.youngchemist.model.user.UserProgress
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LectureUi(
    var lectureId: String = "",
    var collectionId: String = "",
    var lectureTitle: String = "",
    var lectureDescription: String = "",
    var lectureOrderNumber: Int = 0,
    var data: ArrayList<String> = arrayListOf(),
    var isTestEnabled: Boolean = false,
    var mark: Double = 0.0,
    var test: Test? = null,
    var userProgress: UserProgress? = null
) : Parcelable {

    fun addUserPassedTests(userPassedTests: List<PassedUserTest>) {
        test?.let { test ->
            isTestEnabled = true
            val passedUserTest = userPassedTests.find { passedUserTest ->
                test.testId == passedUserTest.testUid
            }
            passedUserTest?.let {
                isTestEnabled = false
                mark = it.mark
            }
        }
    }

    fun addUserProgress(userProgressList: List<UserProgress>): Boolean {
        userProgressList.find { userProgress ->
            userProgress.lectureId == lectureId
        }.also {
            return if (it == null) {
                false
            } else {
                userProgress = it
                true
            }
        }
    }
}