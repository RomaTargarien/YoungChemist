package com.example.youngchemist.model.user

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "userProgress")
@Parcelize
data class UserProgress(
    var userId: String = "",
    var lectureId: String = "",
    var lastReadenPage: Int = 0
): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var userProgressPrimaryKey: Int = 0
}