package com.chemist.youngchemist.model

import com.chemist.youngchemist.model.user.UserAchievement

data class Achievement(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val testsToDone: Int? = null,
    val testsMark: Int? = null,
    val testsAverageMark: Int? = null,
    val lecturesToRead: Int? = null,
    val modelsToSave: Int? = null,
    val registered: Boolean? = null
) {
    fun convertToUserAchievement(userId: String): UserAchievement {
        val userAchievement = UserAchievement(id,userId, title, imageUrl)
        testsToDone?.let {
            userAchievement.itemsToDone = it
        }
        lecturesToRead?.let {
            userAchievement.itemsToDone = it
        }
        modelsToSave?.let {
            userAchievement.itemsToDone = it
        }
        registered?.let {
            userAchievement.itemsToDone = 1
        }
        return userAchievement
    }
}