package com.example.dailytasks.core.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

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
