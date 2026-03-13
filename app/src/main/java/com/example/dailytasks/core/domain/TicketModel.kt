package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    OVERDUE
}

interface Ticket {
    val date : LocalDate
    val time : LocalTime
    val status : TaskStatus
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
    override val status : TaskStatus = TaskStatus.PENDING,
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
    val isCompleted : TaskStatus = TaskStatus.PENDING,
    val id : String,
)

fun TicketModel.toDto() = DayTicketDTO(
    date = date,
    taskId = taskId,
    time = time,
    isCompleted = status,
    id = ticketId
)

fun DayTicketDTO.toDomain() = TicketModel(
    date = date,
    taskId = taskId,
    time = time,
    status = isCompleted,
    ticketId = id
)

