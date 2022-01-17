package com.example.youngchemist.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.youngchemist.model.UserInfo

@Entity(tableName = "saved3DModels")
data class Model3D(
    var userId: String = "",
    var modelId: String = "",
    var modelUri: String = "",
    var modelTitle: String = "",
    var addingDate: String = "",
): UserInfo {
    @PrimaryKey(autoGenerate = true)
    var modelPrimaryKey: Int = 0
    var uploaded: Boolean = false
}