package com.example.dailytasks.viewModel

import android.os.Build
import android.util.Log
import com.example.dailytasks.model.DayTicketModel
import java.time.Clock
import java.time.LocalDate

class TaskRepository {
    fun getTicketsByDate(date : LocalDate) : List<DayTicketModel> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            listOf(
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
                ))
        } else {
            Log.e("TaskRepository", "API level is too low")
            listOf()
        }
    }
}