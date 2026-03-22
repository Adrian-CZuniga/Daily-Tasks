package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Modelo que representa una tarea recurrente con un posible límite de fecha.
 */
data class TaskSequenceLimitModel(
    override val name : String,
    val schedule : Map<DayOfWeek, List<LocalTime>>,
    val limitDate : LocalDate? = null,
    override val type: TypeTask = TypeTask.PERSONAL,
    override val id : String,
) : TaskModel(id) {
    /**
     * Genera la lista de tickets para un rango de fechas específico.
     */
    fun createDayTicketModels(fromDate : LocalDate, toDate : LocalDate): List<TicketModel> {
        if (limitDate != null) {
            if (fromDate.isAfter(limitDate)) return listOf()
            if (toDate.isAfter(limitDate)) return createDayTicketModels(fromDate, limitDate)
        }
        val dayTickets = mutableListOf<TicketModel>()

        val rangeDays = fromDate.datesUntil(toDate)
        rangeDays.forEach { date ->
            val times = schedule[date.dayOfWeek] ?: listOf()
            times.forEach { time ->
                dayTickets.add(
                    TicketModel(
                        date = date,
                        time = time,
                        status = TaskStatus.PENDING,
                        ticketId = TicketModel.createId(id, date, time),
                        taskId = id
                    )
                )
            }
        }

        return dayTickets
    }
}

/**
 * DTO para la serialización de tareas recurrentes.
 */
@Serializable
data class TaskSequenceLimitDto(
    val id: String,
    val name: String,
    val type: String,
    val schedule: Map<String, List<@Serializable(with = LocalTimeSerializer::class) LocalTime>>,
    @Serializable(with = LocalDateSerializer::class) val limitDate: LocalDate? = null
)

/**
 * Convierte el modelo de dominio a DTO.
 */
fun TaskSequenceLimitModel.toDto() = TaskSequenceLimitDto(
    id = id,
    name = name,
    type = type.name,
    schedule = schedule.mapKeys { it.key.name },
    limitDate = limitDate
)

/**
 * Convierte el DTO a modelo de dominio.
 */
fun TaskSequenceLimitDto.toDomain() = TaskSequenceLimitModel(
    id = id,
    name = name,
    type = TypeTask.valueOf(type),
    schedule = schedule.mapKeys { DayOfWeek.valueOf(it.key) },
    limitDate = limitDate
)
