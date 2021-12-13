package com.example.youngchemist.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unsavedTests")
data class PassedUserTest(
    var testUid: String = "",
    var userUid: String = "",
    var mark: Double = 0.0,
    var answers: ArrayList<AnswerUser> = arrayListOf(),
    var wasTestUploaded: Boolean = true
) {
    @PrimaryKey(autoGenerate = true)
    var testPrimaryKey: Int = 0
}