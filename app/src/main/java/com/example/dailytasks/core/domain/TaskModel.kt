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
    val date : LocalDate,
    val originTaskId : String,
    val time : LocalTime,
    val isCompleted : Boolean = false,
    val id : String,
){
    companion object {
        fun createId(originTaskId : String, date : LocalDate, time : LocalTime) : String {
            return "ticket_${originTaskId}_${date}_${time}"
        }
    }
}

@Serializable
data class DayTicketDTO(
    val date : @Serializable(with = LocalDateSerializer::class) LocalDate,
    val originTaskId : String,
    val time : @Serializable(with = LocalTimeSerializer::class) LocalTime,
    val isCompleted : Boolean = false,
    val id : String,
)

fun DayTicketModel.toDto() = DayTicketDTO(
    date = date,
    originTaskId = originTaskId,
    time = time,
    isCompleted = isCompleted,
    id = id
)

fun DayTicketDTO.toDomain() = DayTicketModel(
    date = date,
    originTaskId = originTaskId,
    time = time,
    isCompleted = isCompleted,
    id = id
)

