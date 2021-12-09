package com.example.youngchemist.model.ui

import android.os.Parcelable
import com.example.youngchemist.model.Test
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LectureUi(
    var lectureId: String = "",
    var collectionId: String = "",
    var lectureTitle: String = "",
    var lectureDescription: String ="",
    var data: ArrayList<String> = arrayListOf(),
    var isTestEnabled: Boolean = false,
    var mark: Double = 0.0,
    var lectureWasReaden: Boolean = false,
    var test: Test? = null
): Parcelable