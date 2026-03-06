package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

abstract class TaskModel() {
    abstract val name : String
    abstract val type : TypeTask
    abstract val id : String

}




// TaskModel 1:M DayTicketModel
data class DayTicketModel(
    val name : String,
    val date : LocalDate,
    val type : TypeTask = TypeTask.PERSONAL,
    val originTaskId : String,
    val time : LocalTime,
    val isCompleted : Boolean = false,
    val id : String,
)

@Serializable
data class DayTicketDTO(
    val name : String,
    val date : @Serializable(with = LocalDateSerializer::class) LocalDate,
    val type : TypeTask = TypeTask.PERSONAL,
    val originTaskId : String,
    val time : @Serializable(with = LocalTimeSerializer::class) LocalTime,
    val isCompleted : Boolean = false,
    val id : String,
)

fun DayTicketModel.toDto() = DayTicketDTO(
    name = name,
    date = date,
    type = type,
    originTaskId = originTaskId,
    time = time,
    isCompleted = isCompleted,
    id = id
)

fun DayTicketDTO.toDomain() = DayTicketModel(
    name = name,
    date = date,
    type = type,
    originTaskId = originTaskId,
    time = time,
    isCompleted = isCompleted,
    id = id
)

