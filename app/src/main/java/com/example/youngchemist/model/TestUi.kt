package com.example.youngchemist.model

import android.util.Log

data class TestUi(
    var userUid: String = "",
    var testUid: String = "",
    var tasksUi: ArrayList<TaskUi> = arrayListOf()
) {
    fun evaluateMark(test: Test): PassedUserTest {
        val passedUserTest = PassedUserTest()
        for (item in tasksUi) {
            val rightAnswers = test.rightAnswersPositions(item.position)
            val userAnswers = userAnswersPosition(item.position)
            Log.d("TAG",item.position.toString() + "" +"-  Right " +rightAnswers.toString() +
                    " User " + userAnswers.toString())
        }
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