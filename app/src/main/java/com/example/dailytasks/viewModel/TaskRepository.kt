package com.example.dailytasks.viewModel

import com.example.dailytasks.model.DayTicketModel
import com.example.dailytasks.model.TaskSequenceLimitModel
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TaskRepository {
    fun getTicketsByDate(date : LocalDate) : List<DayTicketModel> {
        return listOf(
                DayTicketModel(
                    name = "Task 1",
                    date = LocalDate.now(Clock.systemDefaultZone()).atTime(2, 30),
                    originTaskId = "1",
                    id = "1"
                ),
                DayTicketModel(
                    name = "Task 2",
                    date = LocalDate.now(Clock.systemDefaultZone()).atTime(6, 0),
                    originTaskId = "2",
                    id = "2"
                ),
                DayTicketModel(
                    name = "Task 3",
                    date = LocalDate.now(Clock.systemDefaultZone()).atTime(10, 0),
                    originTaskId = "3",
                    id = "3"
                ),
                DayTicketModel(
                    name = "Task 4",
                    date = LocalDate.now(Clock.systemDefaultZone()).atTime(14, 0),
                    originTaskId = "4",
                    id = "4"
                )).filter { it.date.toLocalDate() == date }
    }

    fun getTaskSequenceLimitModel() : TaskSequenceLimitModel{
        return TaskSequenceLimitModel(
            name = "Task 1",
            schedule = mapOf(
                DayOfWeek.THURSDAY to listOf(LocalTime.of(12, 30),
                    LocalTime.of(16, 0)),
                DayOfWeek.TUESDAY to listOf(LocalTime.of(12, 30)),
                DayOfWeek.FRIDAY to listOf(LocalTime.of(12, 30))
            ),
            limitDate = LocalDate.now(Clock.systemDefaultZone()).plusDays(14),
            id = "1"
        )
    }
}