package com.example.dailytasks.core.data

import com.example.dailytasks.core.domain.DailyTaskModel
import com.example.dailytasks.core.domain.TaskModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ITaskRepository {
    /**
     * Obtiene un flujo de datos paginados de tickets para un día específico.
     */
    fun getDailyTasks(day: LocalDate): Flow<List<DailyTaskModel>>

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