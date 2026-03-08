package com.example.dailytasks.core.domain

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dailytasks.core.utils.FileTicketsPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import kotlin.collections.emptyList

class TaskManager(private val context: Context) {
    fun getPagedTickets(fromDate: LocalDate): Flow<PagingData<TicketModel>> {
        return Pager(PagingConfig(pageSize = 10)) {
            FileTicketsPagingSource(context, fromDate, pageSize = 10)
        }.flow
    }

    fun saveTask(task: TaskModel) {
        val jsonString = when (task) {
            is TaskSequenceLimitModel -> json.encodeToString(task.toDto())
            is TaskSingleModel -> json.encodeToString(task.toDto())
            else -> {
                throw IllegalArgumentException("Invalid task type")
            }
        }
        writeTaskFile(task.id, jsonString)

        createDayTicketModel(task)
    }

    private fun createDayTicketModel(task: TaskModel){
        val dailyTickets = when (task) {
            is TaskSingleModel -> listOf(task.createDayTicketModel())

            is TaskSequenceLimitModel -> {
                val now = LocalDate.now()
                val toDate = now.plusDays(30)

                task.createDayTicketModels(now, toDate)
            }
            else -> emptyList()
        }

        dailyTickets.forEach {
            saveDayTicketModel(it)
        }
    }

    private fun saveDayTicketModel(ticketModel: TicketModel){
        val ticketDTO = ticketModel.toDto()
        val json = json.encodeToString(ticketDTO)

        writeTicketFile(ticketModel.ticketId, json, ticketModel.date)
    }

    private fun getTicketDirectoryByDay(byDay : LocalDate) : File {
        val dir = File(context.filesDir, "tickets")

        if (!dir.exists()) {
            dir.mkdirs()
        }
        val dayDir = File(dir, byDay.toString())

        if (!dayDir.exists()) {
            dayDir.mkdirs()
        }
        return dayDir
    }

    private fun writeTicketFile(id: String, json: String, dayTask: LocalDate) {
        val dayDir = getTicketDirectoryByDay(dayTask)
        val file = File(dayDir, "$id.json")

        file.writeText(json)

    }



    private fun getTasksDirectory(): File {
        val dir = File(context.filesDir, "tasks_configurations")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    private fun writeTaskFile(id: String, json: String) {

        val tasksDir = getTasksDirectory()

        val file = File(tasksDir, "$id.json")

        file.writeText(json)
    }
    companion object JsonManager {
        private val json = Json {
            ignoreUnknownKeys = true
        }
    }
}
