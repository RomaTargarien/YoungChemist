package com.example.youngchemist.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Test(
    var testId: String = "",
    var testTitle: String = "",
    val tasks: ArrayList<Task> = arrayListOf(),
    val timeInMillis: Long = 0
): Parcelable {

    fun rightAnswersPositions(questionNumber: Int): List<Int> {
        val rightAnswers = mutableListOf<Int>()
        for (answer in tasks[questionNumber].answers) {
            if (answer.itIsRight) {
                rightAnswers.add(answer.position)
            }
        }
        return rightAnswers
    }
}
