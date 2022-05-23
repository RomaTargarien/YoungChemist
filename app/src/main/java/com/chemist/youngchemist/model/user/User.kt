package com.chemist.youngchemist.model

import com.chemist.youngchemist.model.user.Model3D
import com.chemist.youngchemist.model.user.PassedUserTest
import com.chemist.youngchemist.model.user.UserAchievement
import com.chemist.youngchemist.model.user.UserProgress

data class User(
    val uid: String = "",
    var name: String = "",
    var surname: String = "",
    var passedUserTests: ArrayList<PassedUserTest> = arrayListOf(),
    var userProgress: ArrayList<UserProgress> = arrayListOf(),
    var saved3DModels: ArrayList<Model3D> = arrayListOf(),
    var doneAchievements: ArrayList<UserAchievement> = arrayListOf()
)




