package com.example.dailytasks.core.domain

import android.content.Context
import com.example.dailytasks.core.utils.writeAtomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import kotlin.collections.emptyList

class TaskManager(private val context: Context) {

    private var taskCache: MutableMap<String, TaskModel>? = null

    fun getDailyTasks(day: LocalDate): Flow<List<DailyTaskModel>> = flow {
        val tickets = withContext(Dispatchers.IO) { readTickets(day) }

        val taskMap = getTaskCache()
        val tasks = tickets.mapNotNull { ticket ->
            val task = taskMap[ticket.taskId] ?: return@mapNotNull null
            buildDailyTask(ticket, task)
        }

        emit(tasks)
    }

    private fun buildDailyTask(
        ticket: TicketModel,
        task: TaskModel
    ): DailyTaskModel {
        return DailyTaskModel(
            name = task.name,
            type = task.type,

            date = ticket.date,
            time = ticket.time,
            status = ticket.status,
            ticketId = ticket.ticketId,

            id = task.id
        )
    }

    private fun getTaskCache(): MutableMap<String, TaskModel> {
        if (taskCache == null) {
            taskCache = loadAllTasks()
        }
        return taskCache!!
    }

    private fun loadAllTasks(): MutableMap<String, TaskModel> {
        val dir = getTasksDirectory()
        val files = dir.listFiles() ?: return mutableMapOf()

        return files.mapNotNull { file ->
            runCatching {

                val jsonString = file.readText()

                val task = if (jsonString.contains("schedule")) {
                    json.decodeFromString<TaskSequenceLimitDto>(jsonString).toDomain()
                } else {
                    json.decodeFromString<TaskSingleDto>(jsonString).toDomain()
                }

                task.id to task

            }.getOrNull()

        }.toMap().toMutableMap()
    }

    fun saveTask(task: TaskModel) {
        val jsonString = when (task) {
            is TaskSequenceLimitModel -> json.encodeToString(task.toDto())
            is TaskSingleModel -> json.encodeToString(task.toDto())
            else -> throw IllegalArgumentException("Invalid task type")
        }

        writeTaskFile(task.id, jsonString)

        // actualizar cache
        getTaskCache()[task.id] = task

        createTicketModel(task)
    }

    private fun createTicketModel(task: TaskModel) {
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
            saveTicketModel(it)
        }
    }

    private fun saveTicketModel(ticketModel: TicketModel) {
        val ticketDTO = ticketModel.toDto()
        val json = json.encodeToString(ticketDTO)

        writeTicketFile(ticketModel.ticketId, json, ticketModel.date)
    }

    private fun readTickets(day: LocalDate): List<TicketModel> {
        val dayDir = getTicketDirectoryByDay(day)

        val files = dayDir.listFiles() ?: return emptyList()

        return files.mapNotNull { file ->
            runCatching {

                val jsonString = file.readText()

                val dto = json.decodeFromString<DayTicketDTO>(jsonString)

                dto.toDomain()

            }.getOrNull()
        }
    }

    private fun getTicketDirectoryByDay(byDay: LocalDate): File {
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

        file.writeAtomic(json)
    }

    companion object JsonManager {
        private val json = Json {
            ignoreUnknownKeys = true
        }
    }
}