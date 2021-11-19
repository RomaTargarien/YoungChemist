package com.example.youngchemist.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.Nullable

@Entity(tableName = "lectures")
data class Lecture(
    @PrimaryKey
    var title: String = "",
    var subjectTitle: String = "",
    var pages: List<Page> = emptyList()
)

data class Page(
    var data: String = ""
)