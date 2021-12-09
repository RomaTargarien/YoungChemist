package com.example.youngchemist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.youngchemist.model.ui.LectureUi
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "lectures")
@Parcelize
data class Lecture(
    @PrimaryKey
    var lectureId: String = "",
    var collectionId: String = "",
    var lectureTitle: String = "",
    var lectureDescription: String = "",
    var data: ArrayList<String> = arrayListOf(),
    var test: Test? = null
) : Parcelable {

    fun convertToLectureUi() = LectureUi(
        lectureId,
        collectionId,
        lectureTitle,
        lectureDescription,
        data,
        false,
        test = test
    )
}
