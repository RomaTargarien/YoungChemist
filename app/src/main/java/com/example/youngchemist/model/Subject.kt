package com.example.youngchemist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "subjects")
@Parcelize
data class Subject(
    val subjectId: String = "",
    val title: String = "",
    val icon_url: String = "",
    val collectionId: String = ""
): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var subjectPrimaryKey: Int = 0
}