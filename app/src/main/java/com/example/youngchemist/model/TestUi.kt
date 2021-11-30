package com.example.youngchemist.model

import android.util.Log
import com.example.youngchemist.ui.util.roundMark
import java.math.RoundingMode
import java.text.DecimalFormat

data class TestUi(
    var userUid: String = "",
    var testUid: String = "",
    var tasksUi: ArrayList<TaskUi> = arrayListOf()
) {
    fun formatUserPassedTest(test: Test): PassedUserTest {
        val passedUserTest = PassedUserTest()
        var mark = 0.0
        val userAnswersList = arrayListOf<AnswerUser>()
        for (item in tasksUi) {
            val answer = AnswerUser()
            answer.questionPosition = item.position
            val rightAnswers = test.rightAnswersPositions(item.position)
            val userAnswers = userAnswersPosition(item.position)
            if (rightAnswers == userAnswers) {
                mark += 1
                answer.itIsRight = true
            }
            userAnswersList.add(answer)
        }
        passedUserTest.answers = userAnswersList
        Log.d("TAG",mark.toString())
        val del = mark/tasksUi.size
        Log.d("TAG",del.toString())
        val roundedMark: Double = Math.round(del*100.0)/10.0
        passedUserTest.mark = roundedMark
        Log.d("TAG","$roundedMark")
        return passedUserTest
    }

    fun userAnswersPosition(questionNumber: Int): List<Int> {
        val usersAnswers = mutableListOf<Int>()
        for (answer in tasksUi[questionNumber].answersList) {
            if (answer.itIsRight) {
                usersAnswers.add(answer.position)
            }
        }
        return usersAnswers
    }
}

data class TaskUi(
    var position: Int = -1,
    var answersList: List<AnswerUi> = listOf()
)

data class AnswerUi(
    var position: Int = -1,
    var itIsRight: Boolean = false
)