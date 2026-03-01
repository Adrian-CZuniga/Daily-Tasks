package com.example.dailytasks.core.domain

import java.time.LocalDateTime

data class TaskSingleModel(
    override val name : String,
    val date : LocalDateTime,
    override val type: TypeTask = TypeTask.PERSONAL,
    override val id : String,
) : TaskModel()

fun TaskSingleModel.generateDayTicketModel() : DayTicketModel {
    return DayTicketModel(
        name = this.name,
        date = this.date.toLocalDate(),
        time =  this.date.toLocalTime(),
        originTaskId = this.id,
        id = this.id
    )
}
