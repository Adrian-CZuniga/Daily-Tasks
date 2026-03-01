package com.example.dailytasks.core.domain

import java.time.LocalDate
import java.time.LocalTime

abstract class TaskModel() {
    abstract val name : String
    abstract val type : TypeTask
    abstract val id : String
}

// TaskModel 1:M DayTicketModel
data class DayTicketModel(
    val name : String,
    val date : LocalDate,
    val type : TypeTask = TypeTask.PERSONAL,
    val originTaskId : String,
    val time : LocalTime,
    val isCompleted : Boolean = false,
    val id : String,
)
