package com.example.dailytasks.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

abstract class TaskModel() {
    abstract val name : String
    abstract val id : String
}

data class TaskSequenceLimitModel(
    override val name : String,
    val schedule : Map<DayOfWeek, List<LocalTime>>,
    val limitDate : LocalDate? = null, // null == no limit == infinite
    override val id : String,
) : TaskModel()

data class TaskSingleModel(
    override val name : String,
    val date : LocalDateTime,
    override val id : String,
) : TaskModel()

data class DayTicketModel(
    val name : String,
    val date : LocalDateTime,
    val originTaskId : String,
    val id : String,
)
