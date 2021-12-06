package com.example.youngchemist.model


data class Test(
    var testId: String = "",
    var testTitle: String = "",
    val tasks: ArrayList<Task> = arrayListOf(),
    val timeInMillis: Long = 0
) {

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