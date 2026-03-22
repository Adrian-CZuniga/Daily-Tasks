package com.example.dailytasks.core.domain

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

/**
 * Clase base abstracta para todas las definiciones de tareas.
 */
abstract class TaskModel(open val id: String) {
    abstract val name: String
    abstract val type: TypeTask

    companion object {
        /**
         * Crea un identificador único para una nueva tarea.
         */
        fun createId(name: String, type: TypeTask): String {
            return "task_${UUID.randomUUID()}"
        }
    }
}
