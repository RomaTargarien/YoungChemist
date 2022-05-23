package com.chemist.youngchemist.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chemist.youngchemist.model.UserInfo

@Entity(tableName = "unsavedTests")
data class PassedUserTest(
    var testUid: String = "",
    var userUid: String = "",
    var mark: Double = 0.0,
    var answers: ArrayList<AnswerUser> = arrayListOf(),
    var wasTestUploaded: Boolean = true
): UserInfo {
    @PrimaryKey(autoGenerate = true)
    var testPrimaryKey: Int = 0
}