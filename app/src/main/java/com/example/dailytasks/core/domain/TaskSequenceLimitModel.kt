package com.example.dailytasks.core.domain

import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime


data class TaskSequenceLimitModel(
    override val name : String,
    val schedule : Map<DayOfWeek, List<LocalTime>>,
    val limitDate : LocalDate? = null, // null == no limit == infinite
    override val type: TypeTask = TypeTask.PERSONAL,
    override val id : String,
) : TaskModel() {
    fun createDayTicketModels(fromDate : LocalDate, toDate : LocalDate): List<TicketModel> {
        if (fromDate > limitDate) return listOf()
        if (toDate > limitDate && limitDate != null) return createDayTicketModels(fromDate, limitDate)

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


@Serializable
data class TaskSequenceLimitDto(
    val id: String,
    val name: String,
    val type: String, // Store enum as String
    val schedule: Map<String, List<@Serializable(with = LocalTimeSerializer::class) LocalTime>>,
    @Serializable(with = LocalDateSerializer::class) val limitDate: LocalDate? = null
)



// --- Mapping Extensions ---
fun TaskSequenceLimitModel.toDto() = TaskSequenceLimitDto(
    id = id,
    name = name,
    type = type.name,
    schedule = schedule.mapKeys { it.key.name }, // DayOfWeek to String
    limitDate = limitDate
)

fun TaskSequenceLimitDto.toDomain() = TaskSequenceLimitModel(
    id = id,
    name = name,
    type = TypeTask.valueOf(type),
    schedule = schedule.mapKeys { DayOfWeek.valueOf(it.key) },
    limitDate = limitDate
)
