package com.example.dailytasks.viewModel

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dailytasks.model.DayTicketModel
import com.example.dailytasks.model.TaskSequenceLimitModel
import com.example.dailytasks.pagination.FileTicketsPagingSource
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TaskRepository(private val context: Context) {

    fun getPagedTickets(fromDate: LocalDate): Flow<PagingData<DayTicketModel>> {
        return Pager(PagingConfig(pageSize = 10)) {
            FileTicketsPagingSource(context, fromDate, pageSize = 10)
        }.flow
    }

    fun getTicketsByRangeDate(fromDate : LocalDate, toDate : LocalDate) : List<DayTicketModel> {

        val tickets = getTaskSequenceLimitModel()
            .generateTicketsModel(fromDate, toDate)
            .toMutableList()

        while (!fromDate.isBefore(toDate)) {
            tickets.addAll(getTaskSingleModel(fromDate))
            fromDate.plusDays(1)
        }

        return tickets.toList()
    }

    fun getTaskSingleModel(date : LocalDate) : List<DayTicketModel> {
        return listOf(
                DayTicketModel(
                    name = "Task 1",
                    date = date,
                    times = listOf(LocalTime.of(12, 30), LocalTime.of(16, 0)),
                    originTaskId = "1",
                    id = "1"
                ),
                DayTicketModel(
                    name = "Task 2",
                    date = date,
                    times = listOf(LocalTime.of(12, 30), LocalTime.of(16, 0)),
                    originTaskId = "2",
                    id = "2"
                ),
                DayTicketModel(
                    name = "Task 3",
                    date = date,
                    times = listOf(LocalTime.of(12, 30), LocalTime.of(16, 0)),
                    originTaskId = "3",
                    id = "3"
                ),
                DayTicketModel(
                    name = "Task 4",
                    date = date,
                    times = listOf(LocalTime.of(12, 30), LocalTime.of(16, 0)),
                    originTaskId = "4",
                    id = "4"
                )).filter { it.date == date }
    }

    fun getTaskSequenceLimitModel() : TaskSequenceLimitModel {
        return TaskSequenceLimitModel(
            name = "Task 1",
            schedule = mapOf(
                DayOfWeek.THURSDAY to listOf(LocalTime.of(12, 30),
                    LocalTime.of(16, 0)),
                DayOfWeek.TUESDAY to listOf(LocalTime.of(12, 30)),
                DayOfWeek.FRIDAY to listOf(LocalTime.of(12, 30))
            ),
            limitDate = LocalDate.now(Clock.systemDefaultZone()).plusMonths(2),
            id = "1"
        )
    }
}