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
            Log.d("TAG","${item.position} - Right $rightAnswers User $userAnswers $answer")
        }
        Log.d("TAG","llll")
        passedUserTest.answers = userAnswersList
        passedUserTest.mark = mark
        Log.d("TAG","kkk")
        //Log.d("TAG",mark.toString())
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