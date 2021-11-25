package com.example.youngchemist.model

data class TaskUi(
    var position: Int,
    var answersList: List<AnswerUi>
)

data class AnswerUi(
    var position: Int,
    var itIsRight: Boolean
)