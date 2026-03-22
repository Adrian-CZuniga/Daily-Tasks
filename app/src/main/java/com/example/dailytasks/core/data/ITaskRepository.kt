package com.example.dailytasks.core.data

import com.example.dailytasks.core.domain.DailyTaskModel
import com.example.dailytasks.core.domain.TaskModel
import com.example.dailytasks.core.domain.TicketModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interfaz del repositorio para la gestión de tareas y tickets.
 */
interface ITaskRepository {
    /**
     * Obtiene un flujo de tareas diarias para una fecha específica.
     */
    fun getDailyTasks(day: LocalDate): Flow<List<DailyTaskModel>>

    /**
     * Guarda una nueva tarea.
     */
    suspend fun saveTask(task: TaskModel)

    /**
     * Actualiza una tarea existente.
     */
    suspend fun updateTask(task: TaskModel)

    /**
     * Obtiene una tarea específica por su identificador único.
     */
    suspend fun getTaskById(taskId: String): TaskModel?

    /**
     * Busca la tarea asociada a un identificador de ticket.
     */
    suspend fun getTaskByTicketId(ticketId: String): TaskModel?

    /**
     * Actualiza la información de un ticket específico.
     */
    suspend fun updateTicket(newTicket: TicketModel)
}
