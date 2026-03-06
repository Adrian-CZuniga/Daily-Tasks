package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class TaskSingleModel(
    override val name : String,
    val date : LocalDateTime,
    override val type: TypeTask = TypeTask.PERSONAL,
    override val id : String,
) : TaskModel() {
    fun createDayTicketModel(): DayTicketModel {
        return DayTicketModel(
            name = this.name,
            date = this.date.toLocalDate(),
            time =  this.date.toLocalTime(),
            originTaskId = this.id,
            id = this.id
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
    date = date.toLocalDate(),
    time = date.toLocalTime()
)

fun TaskSingleDto.toDomain() = TaskSingleModel(
    id = id,
    name = name,
    date = LocalDateTime.of(date, time),
    type = TypeTask.valueOf(type),
)