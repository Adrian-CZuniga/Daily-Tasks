package com.example.dailytasks.core.domain

import android.content.Context
import com.example.dailytasks.core.utils.AppConstants
import com.example.dailytasks.core.utils.observeChanges
import com.example.dailytasks.core.utils.writeAtomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate

/**
 * Gestor central de persistencia de tareas y tickets.
 * Maneja la lógica de almacenamiento en archivos JSON de forma jerárquica.
 */
class TaskManager(private val context: Context) {
    private var taskCache: MutableMap<String, TaskModel>? = null

    /**
     * Obtiene un flujo de tareas diarias para una fecha específica.
     * Observa cambios en el directorio del mes para actualizaciones en tiempo real.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDailyTasks(day: LocalDate): Flow<List<DailyTaskModel>> {
        val monthDir = getMonthDirectory(day)
        val dayFile = getDayFile(day)

        return monthDir.observeChanges().map {
            val tickets = readTicketsFromFile(dayFile)
            val taskMap = getTaskCache()

            tickets.mapNotNull { ticket ->
                val task = taskMap[ticket.taskId] ?: return@mapNotNull null
                buildDailyTask(ticket, task)
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Busca una tarea por su identificador único.
     */
    fun getTaskById(taskId : String) : TaskModel? {
        return getTaskCache()[taskId]
    }

    /**
     * Busca un ticket específico utilizando el índice global.
     */
    fun findTicketById(ticketId: String): TicketModel? {
        val index = getTicketIndex()
        val dateString = index[ticketId] ?: return null

        val date = LocalDate.parse(dateString)
        val file = getDayFile(date)

        return readTicketsFromFile(file).find { it.ticketId == ticketId }
    }

    private fun readTicketsFromFile(dayFile: File): List<TicketModel> {
        if (!dayFile.exists()) return emptyList()

        return runCatching {
            val jsonString = dayFile.readText()
            json.decodeFromString<List<DayTicketDTO>>(jsonString).map { it.toDomain() }
        }.getOrElse { emptyList() }
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

    /**
     * Guarda una tarea y genera sus tickets correspondientes.
     */
    suspend fun saveTask(task: TaskModel) {
        withContext(Dispatchers.IO) {
            val jsonString = when (task) {
                is TaskSequenceLimitModel -> json.encodeToString(task.toDto())
                is TaskSingleModel -> json.encodeToString(task.toDto())
                else -> throw IllegalArgumentException("Tipo de tarea no soportado")
            }

            writeTaskFile(task.id, jsonString)
            getTaskCache()[task.id] = task
            createTicketsForTask(task)
        }
    }

    private fun createTicketsForTask(task: TaskModel) {
        val dailyTickets = when (task) {
            is TaskSingleModel -> listOf(task.createDayTicketModel())
            is TaskSequenceLimitModel -> {
                val now = LocalDate.now()
                val toDate = now.plusDays(30)
                task.createDayTicketModels(now, toDate)
            }
            else -> emptyList()
        }

        dailyTickets.groupBy { it.date }.forEach { (date, tickets) ->
            saveTicketsForDay(date, tickets)
        }
    }

    private fun saveTicketsForDay(date: LocalDate, newTickets: List<TicketModel>) {
        val dayFile = getDayFile(date)
        val existingTickets = readTicketsFromFile(dayFile).toMutableList()

        newTickets.forEach { newTicket ->
            val index = existingTickets.indexOfFirst { it.ticketId == newTicket.ticketId }
            if (index != -1) {
                existingTickets[index] = newTicket
            } else {
                existingTickets.add(newTicket)
            }
            updateIndex(newTicket.ticketId, newTicket.date)
        }

        val jsonString = json.encodeToString(existingTickets.map { it.toDto() })
        dayFile.writeAtomic(jsonString)
    }

    private fun getMonthDirectory(date: LocalDate): File {
        val year = date.year.toString()
        val month = String.format("%02d", date.monthValue)
        val dir = File(context.filesDir, "${AppConstants.TICKETS_DIR}/$year/$month")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun getDayFile(date: LocalDate): File {
        val day = String.format("%02d", date.dayOfMonth)
        return File(getMonthDirectory(date), "$day${AppConstants.JSON_EXT}")
    }

    private fun getTasksDirectory(): File {
        val dir = File(context.filesDir, AppConstants.TASKS_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun writeTaskFile(id: String, json: String) {
        val file = File(getTasksDirectory(), "$id${AppConstants.JSON_EXT}")
        file.writeAtomic(json)
    }

    /**
     * Actualiza el estado de un ticket específico.
     */
    fun updateTicket(newTicket : TicketModel) {
        saveTicketsForDay(newTicket.date, listOf(newTicket))
    }

    private fun getTicketIndex(): Map<String, String> {
        val indexFile = File(context.filesDir, AppConstants.TICKET_INDEX_FILE)
        return if (indexFile.exists()) {
            runCatching {
                json.decodeFromString<TicketIndexDto>(indexFile.readText()).mapping
            }.getOrDefault(emptyMap())
        } else emptyMap()
    }

    /**
     * Actualiza el índice global de tickets para búsquedas rápidas.
     */
    fun updateIndex(ticketId: String, date: LocalDate) {
        val indexFile = File(context.filesDir, AppConstants.TICKET_INDEX_FILE)
        val currentIndex = getTicketIndex().toMutableMap()
        currentIndex[ticketId] = date.toString()
        indexFile.writeAtomic(json.encodeToString(TicketIndexDto(currentIndex)))
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
    }
}
