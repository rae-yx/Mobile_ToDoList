package com.example.finalproject.utils

data class TaskData(
    val taskId:String,
    var task: String,
    var date: String = "",
    var time: String = "",
    var imageUri: String?= null,
    var description: String = "",
    var isChecked: Boolean = false
)
