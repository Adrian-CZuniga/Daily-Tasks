package com.example.dailytasks.core.data

import com.example.dailytasks.core.domain.DailyTaskModel
import com.example.dailytasks.core.domain.TaskManager
import com.example.dailytasks.core.domain.TaskModel
import com.example.dailytasks.core.domain.TaskStatus
import com.example.dailytasks.core.domain.Ticket
import com.example.dailytasks.core.domain.TicketModel
import com.example.dailytasks.core.domain.toTicketModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskManager: TaskManager) : ITaskRepository {
    override fun getDailyTasks(day: LocalDate): Flow<List<DailyTaskModel>> {
        return taskManager.getDailyTasks(day)
    }

    override suspend fun saveTask(task: TaskModel) {
        taskManager.saveTask(task)
    }

    override suspend fun getTaskById(taskId: String): TaskModel? {
        // Implementación pendiente
        return null
    }

    override suspend fun deleteTask(taskId: String) {
        // Implementación pendiente
    }

    override suspend fun getAllTasks(): List<TaskModel> {
        // Implementación pendiente
        return emptyList()
    }

    override suspend fun updateTicket(newTicket: TicketModel) {
        taskManager.updateTicket(newTicket)
    }
}