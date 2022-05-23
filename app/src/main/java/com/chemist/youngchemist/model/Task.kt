package com.chemist.youngchemist.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    var question: String = "",
    var answers: ArrayList<Answer> = arrayListOf(),
    var imageToQuestionUrl: String = "",
    var multipleAnswersAvailable: Boolean = false
): Parcelable