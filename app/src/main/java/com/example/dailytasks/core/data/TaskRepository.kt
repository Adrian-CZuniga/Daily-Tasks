package com.example.dailytasks.core.data

import com.example.dailytasks.core.domain.DailyTaskModel
import com.example.dailytasks.core.domain.TaskManager
import com.example.dailytasks.core.domain.TaskModel
import com.example.dailytasks.core.domain.TicketModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Implementación del repositorio de tareas.
 * Actúa como mediador entre la lógica de negocio y el gestor de archivos.
 */
class TaskRepository @Inject constructor(
    private val taskManager: TaskManager
) : ITaskRepository {

    override fun getDailyTasks(day: LocalDate): Flow<List<DailyTaskModel>> {
        return taskManager.getDailyTasks(day)
    }

    override suspend fun saveTask(task: TaskModel) {
        taskManager.saveTask(task)
    }

    override suspend fun updateTask(task: TaskModel) {
        taskManager.saveTask(task)
    }

    override suspend fun getTaskById(taskId: String): TaskModel? {
        return taskManager.getTaskById(taskId)
    }

    override suspend fun getTaskByTicketId(ticketId: String): TaskModel? {
        val ticket = taskManager.findTicketById(ticketId)
        return ticket?.taskId?.let { getTaskById(it) }
    }

    override suspend fun updateTicket(newTicket: TicketModel) {
        taskManager.updateTicket(newTicket)
    }
}
