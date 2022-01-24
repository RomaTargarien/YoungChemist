package com.example.youngchemist.model

import com.example.youngchemist.model.user.UserAchievement

data class Achievement(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val testsToDone: Int? = null,
    val testsMark: Int? = null,
    val testsAverageMark: Int? = null,
    val lecturesToRead: Int? = null,
    val modelsToSave: Int? = null,
    val isRegistered: Boolean? = null
) {
    fun convertToUserAchievement(): UserAchievement {
        val userAchievement = UserAchievement(id, title, imageUrl)
        testsToDone?.let {
            userAchievement.itemsToDone = it
        }
        lecturesToRead?.let {
            userAchievement.itemsToDone = it
        }
        modelsToSave?.let {
            userAchievement.itemsToDone = it
        }
        isRegistered?.let {
            userAchievement.itemsToDone = 1
        }
        return userAchievement
    }
}