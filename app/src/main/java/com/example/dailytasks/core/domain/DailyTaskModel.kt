package com.example.dailytasks.core.domain

import java.time.LocalDate
import java.time.LocalTime

data class DailyTaskModel(
    override val name: String,
    override val type: TypeTask,

    override val date: LocalDate,
    override val time: LocalTime,
    override val status: TaskStatus,
    override val ticketId : String,

    override val taskId: String
) : TaskModel(taskId), Ticket

fun DailyTaskModel.toTicketModel() : TicketModel {
    return TicketModel(
        date = date,
        time = time,
        status = status,
        ticketId = ticketId,
        taskId = taskId
    )
}