package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

interface Ticket {
    val date : LocalDate
    val time : LocalTime
    val isCompleted : Boolean
    val ticketId : String

    companion object {
        fun createId(originTaskId : String, date : LocalDate, time : LocalTime) : String {
            return "ticket_${originTaskId}_${date}_${time}"
        }
    }
}
// TaskModel 1:M DayTicketModel
class TicketModel(
    override val date : LocalDate,
    val taskId : String,
    override val time : LocalTime,
    override val isCompleted : Boolean = false,
    override val ticketId : String,
) : Ticket {
    companion object {
        fun createId(originTaskId : String, date : LocalDate, time : LocalTime) : String {
            return "ticket_${originTaskId}_${date}_${time}"
        }
    }
}

@Serializable
data class DayTicketDTO(
    val date : @Serializable(with = LocalDateSerializer::class) LocalDate,
    val taskId : String,
    val time : @Serializable(with = LocalTimeSerializer::class) LocalTime,
    val isCompleted : Boolean = false,
    val id : String,
)

fun TicketModel.toDto() = DayTicketDTO(
    date = date,
    taskId = taskId,
    time = time,
    isCompleted = isCompleted,
    id = ticketId
)

fun DayTicketDTO.toDomain() = TicketModel(
    date = date,
    taskId = taskId,
    time = time,
    isCompleted = isCompleted,
    ticketId = id
)

