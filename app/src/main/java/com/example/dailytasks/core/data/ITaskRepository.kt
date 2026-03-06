package com.example.dailytasks.core.data

import androidx.paging.PagingData
import com.example.dailytasks.core.domain.DayTicketModel
import com.example.dailytasks.core.domain.TaskModel
import com.example.dailytasks.core.domain.TaskSequenceLimitModel
import com.example.dailytasks.core.domain.TaskSingleModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ITaskRepository {
    /**
     * Obtiene un flujo de datos paginados de tickets para un día específico.
     */
    fun getPagedDayTicketsFromDay(day: LocalDate): Flow<PagingData<DayTicketModel>>

    /**
     * Guarda una nueva tarea o actualiza una existente.
     */
    suspend fun saveTask(task: TaskModel)


    /**
     * Obtiene una tarea específica por su ID.
     */
    suspend fun getTaskById(taskId: String): TaskModel?

    /**
     * Elimina una tarea por su ID.
     */
    suspend fun deleteTask(taskId: String)

    /**
     * Obtiene todas las tareas configuradas.
     */
    suspend fun getAllTasks(): List<TaskModel>

    /**
     * Actualiza el estado de completado de un ticket específico.
     */
    suspend fun updateTicketCompletion(ticketId: String, isCompleted: Boolean)
}