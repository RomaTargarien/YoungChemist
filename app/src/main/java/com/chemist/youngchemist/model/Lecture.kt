package com.chemist.youngchemist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chemist.youngchemist.model.ui.LectureUi
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "lectures")
@Parcelize
data class Lecture(
    @PrimaryKey
    var lectureId: String = "",
    var collectionId: String = "",
    var lectureTitle: String = "",
    var lectureDescription: String = "",
    var lectureKeyWord: String ="",
    var data: ArrayList<String> = arrayListOf(),
    var test: Test? = null
) : Parcelable {

    fun convertToLectureUi() = LectureUi(
        lectureId,
        collectionId,
        lectureTitle,
        lectureDescription,
        lectureKeyWord,
        data,
        false,
        test = test
    )
}
