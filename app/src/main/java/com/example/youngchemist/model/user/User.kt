package com.example.youngchemist.model

import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress

data class User(
    val uid: String = "",
    var name: String = "",
    var surname: String = "",
    var passedUserTests: ArrayList<PassedUserTest> = arrayListOf(),
    var userProgress: ArrayList<UserProgress> = arrayListOf(),
    var saved3DModels: ArrayList<Model3D> = arrayListOf(),
    var doneAchievements: ArrayList<UserAchievement> = arrayListOf()
)




