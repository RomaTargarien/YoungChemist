package com.example.youngchemist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import javax.annotation.Nullable

@Entity(tableName = "lectures")
@Parcelize
data class Lecture(
    @PrimaryKey
    var lectureId: String = "",
    var collectionId: String = "",
    var lectureTitle: String = "",
    var data: ArrayList<String> = arrayListOf(),
    var testId: String = "",
): Parcelable