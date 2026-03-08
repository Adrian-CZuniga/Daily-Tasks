package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

data class TaskSingleModel(
    override val name : String,
    val date : LocalDate,
    val time : LocalTime,
    override val type: TypeTask = TypeTask.PERSONAL,
    override val id : String,
) : TaskModel() {
    fun createDayTicketModel(): TicketModel {
        return TicketModel(
            date = date,
            time =  time,
            taskId = this.id,
            ticketId = TicketModel.createId(id, this.date, this.time)
        )
    }
}


@Serializable
data class TaskSingleDto(
    val id: String,
    val name: String,
    val type: String,
    @Serializable(with = LocalDateSerializer::class) val date: LocalDate,
    @Serializable(with = LocalTimeSerializer::class) val time: LocalTime
)



fun TaskSingleModel.toDto() = TaskSingleDto(
    id = id,
    name = name,
    type = type.name,
    date = date,
    time = time
)

fun TaskSingleDto.toDomain() = TaskSingleModel(
    id = id,
    name = name,
    date = date,
    time = time,
    type = TypeTask.valueOf(type)
)