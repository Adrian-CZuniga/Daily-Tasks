package com.example.dailytasks.files

import android.content.Context
import java.io.File

class FileManager(private val context: Context) {
    companion object {
        private const val TASKS_DIR = "tasks"
        private const val TICKETS_DIR = "tickets"
        private const val TASK_FILE_PREFIX = "task_"
        private const val TICKET_FILE_PREFIX = "ticket_"
    }

    private val tasksDir: File by lazy {
        File(context.filesDir, TASKS_DIR).apply { mkdirs() }
    }

    private val ticketsDir: File by lazy {
        File(context.filesDir, TICKETS_DIR).apply { mkdirs() }
    }
}