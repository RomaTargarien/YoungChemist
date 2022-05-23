package com.chemist.youngchemist.model.ui

import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.model.user.AnswerUser
import com.chemist.youngchemist.model.user.PassedUserTest
import kotlin.math.roundToInt

data class TestUi(
    var userUid: String = "",
    var testUid: String = "",
    var tasksUi: ArrayList<TaskUi> = arrayListOf()
) {
    fun formatUserPassedTest(test: Test): PassedUserTest {
        val passedUserTest = PassedUserTest()
        passedUserTest.testUid = test.testId
        passedUserTest.userUid = userUid
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
        val del = mark / tasksUi.size
        val roundedMark: Double = (del * 100.0).roundToInt() / 10.0
        passedUserTest.mark = roundedMark
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



