package com.example.dailytasks.core.data

import androidx.paging.PagingData
import com.example.dailytasks.core.domain.TicketModel
import com.example.dailytasks.core.domain.TaskManager
import com.example.dailytasks.core.domain.TaskModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskManager: TaskManager) : ITaskRepository {
    override fun getPagedDayTicketsFromDay(day: LocalDate): Flow<PagingData<TicketModel>> {
        return taskManager.getPagedTickets(day)
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

    override suspend fun updateTicketCompletion(ticketId: String, isCompleted: Boolean) {
        // Implementación pendiente
    }
}