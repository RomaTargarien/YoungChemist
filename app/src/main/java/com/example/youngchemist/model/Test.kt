package com.example.youngchemist.model


data class Test (
    var lectionId: Int = 0,
    var testId: Int = 0,
    val tasks: ArrayList<Task> = arrayListOf()
)

data class Task(
    var question: String = "",
    var answers: ArrayList<Answer> = arrayListOf()
)

data class Answer(
    var text: String = "",
    var itIsRight: Boolean = false
)