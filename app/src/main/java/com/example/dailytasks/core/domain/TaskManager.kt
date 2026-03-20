package com.example.dailytasks.core.domain

import android.content.Context
import com.example.dailytasks.core.utils.observeChanges
import com.example.dailytasks.core.utils.writeAtomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import kotlin.collections.emptyList

class TaskManager(private val context: Context) {
    private var taskCache: MutableMap<String, TaskModel>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDailyTasks(day: LocalDate): Flow<List<DailyTaskModel>> {
        val dayDir = getTicketDirectoryByDay(day)

        return dayDir.observeChanges().map {
            val tickets = readTicketsFromDir(dayDir)
            val taskMap = getTaskCache()

            tickets.mapNotNull { ticket ->
                val task = taskMap[ticket.taskId] ?: return@mapNotNull null
                buildDailyTask(ticket, task)
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getTaskById(taskId : String) : TaskModel? {
        return getTaskCache()[taskId]
    }


    fun findTicketById(ticketId: String): TicketModel? {
        val index = getTicketIndex()
        val dateString = index[ticketId] ?: return null

        val date = LocalDate.parse(dateString)
        val file = File(getTicketDirectoryByDay(date), "$ticketId.json")

        return if (file.exists()) {
            json.decodeFromString<DayTicketDTO>(file.readText()).toDomain()
        } else null
    }

    private fun readTicketsFromDir(dayDir: File): List<TicketModel> {
        val files = dayDir.listFiles { file -> file.extension == "json" } ?: return emptyList()

        return files.mapNotNull { file ->
            runCatching {
                val jsonString = file.readText()
                json.decodeFromString<DayTicketDTO>(jsonString).toDomain()
            }.getOrNull()
        }
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

            taskId = ticket.taskId
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

    suspend fun saveTask(task: TaskModel) {
        withContext(Dispatchers.IO) {
            val jsonString = when (task) {
                is TaskSequenceLimitModel -> json.encodeToString(task.toDto())
                is TaskSingleModel -> json.encodeToString(task.toDto())
                else -> throw IllegalArgumentException("Invalid task type")
            }

            writeTaskFile(task.id, jsonString)

            getTaskCache()[task.id] = task

            createTicketModel(task)
        }
    }

    suspend fun updateTask(task: TaskModel) {
        withContext(Dispatchers.IO) {
            val jsonString = when (task) {
                is TaskSequenceLimitModel -> json.encodeToString(task.toDto())
                is TaskSingleModel -> json.encodeToString(task.toDto())
                else -> throw IllegalArgumentException("Invalid task type")
            }
            writeTaskFile(task.id, jsonString)

            getTaskCache()[task.id] = task

            createTicketModel(task)
        }
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
        updateIndex(ticketDTO.id, ticketDTO.date)
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

        file.writeAtomic(json)
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

    fun updateTicket(newTicket : TicketModel) {
        val task = getTicketDirectoryByDay(newTicket.date).listFiles()?.find { it.nameWithoutExtension == newTicket.ticketId }
        if (task == null) return

        val ticketDTO = newTicket.toDto()
        val json = json.encodeToString(ticketDTO)

        task.writeAtomic(json)
    }

    private fun getTicketIndex(): Map<String, String> {
        val indexFile = File(context.filesDir, "ticket_index.json")
        return if (indexFile.exists()) {
            runCatching {
                json.decodeFromString<TicketIndexDto>(indexFile.readText()).mapping
            }.getOrDefault(emptyMap())
        } else emptyMap()
    }

    fun updateIndex(ticketId: String, date: LocalDate) {
        val indexFile = File(context.filesDir, "ticket_index.json")

        val currentIndex = getTicketIndex().toMutableMap()
        currentIndex[ticketId] = date.toString()
        indexFile.writeAtomic(json.encodeToString(TicketIndexDto(currentIndex)))
    }


    companion object JsonManager {
        private val json = Json {
            ignoreUnknownKeys = true
        }
    }
}