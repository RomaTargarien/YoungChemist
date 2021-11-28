package com.example.youngchemist.model


data class Test(
    var lectionId: Int = 0,
    var testId: Int = 0,
    var testTitle: String = "",
    val tasks: ArrayList<Task> = arrayListOf()
) {
    fun amountOfRightAnswers(): Double {
        var amountOfRightAnswers = 0
        for (task in tasks) {
            for (answer in task.answers) {
                if (answer.itIsRight) {
                    amountOfRightAnswers++
                }
            }
        }
        return amountOfRightAnswers.toDouble()
    }
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

data class Task(
    var question: String = "",
    var answers: ArrayList<Answer> = arrayListOf(),
    var imageToQuestionUrl: String = "",
    var multipleAnswersAvailable: Boolean = false
)

data class Answer(
    var text: String = "",
    var itIsRight: Boolean = false,
    var position: Int = -1
)