package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

/**
 * Modelo que representa una tarea de ejecución única.
 */
data class TaskSingleModel(
    override val name : String,
    val date : LocalDate,
    val time : LocalTime,
    override val type: TypeTask = TypeTask.PERSONAL,
    override val id : String,
) : TaskModel(id) {
    /**
     * Crea la instancia de ticket para esta tarea única.
     */
    fun createDayTicketModel(): TicketModel {
        return TicketModel(
            date = date,
            time =  time,
            taskId = this.id,
            ticketId = TicketModel.createId(id, this.date, this.time)
        )
    }
}

/**
 * DTO para la serialización de tareas únicas.
 */
@Serializable
data class TaskSingleDto(
    val id: String,
    val name: String,
    val type: String,
    @Serializable(with = LocalDateSerializer::class) val date: LocalDate,
    @Serializable(with = LocalTimeSerializer::class) val time: LocalTime
)

/**
 * Convierte el modelo de dominio a DTO.
 */
fun TaskSingleModel.toDto() = TaskSingleDto(
    id = id,
    name = name,
    type = type.name,
    date = date,
    time = time
)

/**
 * Convierte el DTO a modelo de dominio.
 */
fun TaskSingleDto.toDomain() = TaskSingleModel(
    id = id,
    name = name,
    date = date,
    time = time,
    type = TypeTask.valueOf(type)
)
