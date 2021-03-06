package com.chemist.youngchemist.model.user

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chemist.youngchemist.model.UserInfo
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "userProgress")
@Parcelize
data class UserProgress(
    var userId: String = "",
    var lectureId: String = "",
    var lastReadenPage: Int = 0,
    var isLectureEnabled: Boolean = false,
    var isLectureReaden: Boolean = false
): Parcelable,UserInfo {
    @PrimaryKey(autoGenerate = true)
    var userProgressPrimaryKey: Int = 0
    var uploaded: Boolean = false
}