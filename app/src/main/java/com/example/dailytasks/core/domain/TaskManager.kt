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
import java.time.LocalTime

class TaskManager(private val context: Context) {
    fun getPagedTickets(fromDate: LocalDate): Flow<PagingData<DayTicketModel>> {
        return Pager(PagingConfig(pageSize = 10)) {
            FileTicketsPagingSource(context, fromDate, pageSize = 10)
        }.flow
    }

    fun saveTask(task: TaskModel) {
        val jsonString = when (task) {
            is TaskSequenceLimitModel -> json.encodeToString(task.toDto())
            else -> {
                throw IllegalArgumentException("Invalid task type")
            }
        }
        writeTaskFile(task.id, jsonString)

        createDayTicketModel(task)
    }

    private fun createDayTicketModel(task: TaskModel){
        val dailyTickets = when (task) {
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

    private fun saveDayTicketModel(dayTicketModel: DayTicketModel){
        val ticketDTO = dayTicketModel.toDto()
        val json = json.encodeToString(ticketDTO)

        context.openFileOutput(dayTicketModel.id, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }
    private fun getTasksDirectory(): File {
        val dir = File(context.filesDir, "tasks")

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
