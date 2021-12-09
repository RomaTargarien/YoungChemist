package com.example.youngchemist.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved3DModels")
data class Model3D(
    var userId: String = "",
    @PrimaryKey
    var modelId: String = "",
    var modelUri: String = "",
    var modelTitle: String = ""
)