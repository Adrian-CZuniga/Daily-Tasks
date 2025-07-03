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

fun TaskSequenceLimitModel.generateDayTicketsModel(toDate : LocalDate) : List<DayTicketModel>? {
    if (toDate > limitDate)  return null
    val dayOfWeek = toDate.dayOfWeek
    val schedule = this.schedule[dayOfWeek]

    val dayTickets = mutableListOf<DayTicketModel>()
    if (schedule != null) {
        dayTickets.addAll(schedule.map {
            DayTicketModel(
                name = this.name,
                date = LocalDateTime.of(toDate, it),
                originTaskId = this.id,
                id = this.id
            )
        })
    }
    return dayTickets
}

data class TaskSingleModel(
    override val name : String,
    val date : LocalDateTime,
    override val id : String,
) : TaskModel()

fun TaskSingleModel.generateDayTicketModel() : DayTicketModel {
    return DayTicketModel(
        name = this.name,
        date = this.date,
        originTaskId = this.id,
        id = this.id
    )
}


// TaskModel 1:M DayTicketModel
data class DayTicketModel(
    val name : String,
    val date : LocalDateTime,
    val originTaskId : String,
    val id : String,
)
