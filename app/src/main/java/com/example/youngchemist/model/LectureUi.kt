package com.example.youngchemist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lectureUI",primaryKeys = ["userId", "lectureId"])
data class LectureProgress(
    val userId: String = "",
    val lectureId: String = "",
    val lastReadenPage: Int = 0,
)