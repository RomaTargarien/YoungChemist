package com.example.youngchemist.model.user

data class UserAchievement(
    val id: String = "",
    val title: String = "",
    var imageUrl: String = "",
    var itemsDone: Int = 0,
    var itemsToDone: Int = 0
)
