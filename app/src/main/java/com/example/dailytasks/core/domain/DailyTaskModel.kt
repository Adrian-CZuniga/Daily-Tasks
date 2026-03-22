package com.example.dailytasks.core.domain

import java.time.LocalDate
import java.time.LocalTime

/**
 * Modelo que representa una instancia específica de una tarea para la interfaz de usuario.
 * Combina datos de la definición de la tarea con los datos del ticket diario.
 */
data class DailyTaskModel(
    override val name: String,
    override val type: TypeTask,
    override val date: LocalDate,
    override val time: LocalTime,
    override val status: TaskStatus,
    override val ticketId : String,
    override val taskId: String
) : TaskModel(taskId), Ticket

/**
 * Convierte un modelo de tarea diaria a un modelo de ticket.
 */
fun DailyTaskModel.toTicketModel() : TicketModel {
    return TicketModel(
        date = date,
        time = time,
        status = status,
        ticketId = ticketId,
        taskId = taskId
    )
}
