package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

/**
 * Representa los posibles estados de una tarea.
 */
@Serializable
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    OVERDUE
}

/**
 * Interfaz base para cualquier objeto que represente un ticket de tarea.
 */
interface Ticket {
    val date : LocalDate
    val time : LocalTime
    val taskId: String
    val status : TaskStatus
    val ticketId : String

    companion object {
        /**
         * Genera un identificador único para un ticket basado en sus propiedades.
         */
        fun createId(originTaskId : String, date : LocalDate, time : LocalTime) : String {
            return "ticket_${originTaskId}_${date}_${time}"
        }
    }
}

/**
 * Modelo de datos para un ticket individual.
 * Relación 1:M con TaskModel (Una tarea genera muchos tickets).
 */
class TicketModel(
    override val date : LocalDate,
    override val taskId : String,
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

/**
 * Objeto de transferencia de datos (DTO) para la serialización de tickets.
 */
@Serializable
data class DayTicketDTO(
    val date : @Serializable(with = LocalDateSerializer::class) LocalDate,
    val taskId : String,
    val time : @Serializable(with = LocalTimeSerializer::class) LocalTime,
    val status : TaskStatus = TaskStatus.PENDING,
    val id : String,
)

/**
 * Convierte un modelo de ticket a su representación DTO.
 */
fun TicketModel.toDto() = DayTicketDTO(
    date = date,
    taskId = taskId,
    time = time,
    status = status,
    id = ticketId
)

/**
 * Convierte un DTO de ticket a su modelo de dominio.
 */
fun DayTicketDTO.toDomain() = TicketModel(
    date = date,
    taskId = taskId,
    time = time,
    status = status,
    ticketId = id
)
