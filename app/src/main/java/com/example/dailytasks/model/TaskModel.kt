package com.example.dailytasks.model

import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


enum class TypeTask(val title : String, val color : Color) {
    PERSONAL("Personal", Color.Blue),
    STUDY("Study", Color.Red),
    WORK("Work", Color.Green),
    HEALTH("Health", Color.Yellow),
    OTHER("Other", Color.Gray)
}


abstract class TaskModel() {
    abstract val name : String
    abstract val type : TypeTask
    abstract val id : String
}

data class TaskSequenceLimitModel(
    override val name : String,
    val schedule : Map<DayOfWeek, List<LocalTime>>,
    val limitDate : LocalDate? = null, // null == no limit == infinite
    override val type: TypeTask = TypeTask.PERSONAL,
    override val id : String,
) : TaskModel()


fun TaskSequenceLimitModel.generateTicketsModel(fromDate : LocalDate, toDate : LocalDate = fromDate.plusDays(7)) : List<DayTicketModel> {
    if (fromDate > limitDate) return listOf()
    if (toDate > limitDate && limitDate != null) return generateTicketsModel(fromDate, limitDate)

    val dayTickets = mutableListOf<DayTicketModel>()

    val rangeDays = fromDate.datesUntil(toDate)
    rangeDays.forEach {
        val times = schedule[it.dayOfWeek] ?: listOf()
        times.forEach { time ->
            dayTickets.add(
                DayTicketModel(
                    name = this.name,
                    date = it,
                    time = time,
                    originTaskId = this.id,
                    id = "${this.id}_${it}_${time}"
                )
            )
        }
    }

    return dayTickets
}

fun TaskSequenceLimitModel.generateDayTicketModel(date : LocalDate) : List<DayTicketModel> {
    return generateTicketsModel(fromDate = date, toDate = date.plusDays(1))
        .filter { date == it.date }
}


data class TaskSingleModel(
    override val name : String,
    val date : LocalDateTime,
    override val type: TypeTask = TypeTask.PERSONAL,
    override val id : String,
) : TaskModel()

fun TaskSingleModel.generateDayTicketModel() : DayTicketModel {
    return DayTicketModel(
        name = this.name,
        date = this.date.toLocalDate(),
        time =  this.date.toLocalTime(),
        originTaskId = this.id,
        id = this.id
    )
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
