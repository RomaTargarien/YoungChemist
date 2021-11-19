package com.example.youngchemist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey
    val title: String = "",
    val icon_url: String = "",
    var iconByteArray: ByteArray = byteArrayOf()
)