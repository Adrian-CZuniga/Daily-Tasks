package com.example.dailytasks.core.domain

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dailytasks.core.utils.FileTicketsPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

class TaskManager(private val context: Context) {

    fun getPagedTickets(fromDate: LocalDate): Flow<PagingData<DayTicketModel>> {
        return Pager(PagingConfig(pageSize = 10)) {
            FileTicketsPagingSource(context, fromDate, pageSize = 10)
        }.flow
    }


    fun saveTask(task: TaskModel) {
        when (task) {
            is TaskSequenceLimitModel -> saveTaskSequenceLimitModel(task)
            is TaskSingleModel -> saveTaskSingleModel(task)
            else -> {
                return
            }
        }.apply {
            createDayTicketModel(task)
        }
    }

    private fun saveTaskSequenceLimitModel(task: TaskSequenceLimitModel) {
        // Lógica para guardar una tarea de secuencia limitada
        val taskDTO = task.toDto()
        val json = json.encodeToString(taskDTO)

        context.openFileOutput(task.id, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    private fun saveTaskSingleModel(task: TaskSingleModel) {
        // Lógica para guardar una tarea única
        val taskDTO = task.toDto()
        val json = json.encodeToString(taskDTO)
        context.openFileOutput(task.id, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }


    private fun createDayTicketModel(task: TaskModel){
        val dailyTickets = when (task) {
            is TaskSequenceLimitModel -> {
                val now = LocalDate.now()
                val toDate = now.plusDays(30)

                task.createDayTicketModels(now, toDate)
            }
            is TaskSingleModel -> listOf(task.createDayTicketModel())
            else -> emptyList()
        }

        dailyTickets.forEach {
            saveDayTicketModel(it)
        }
    }

    private fun saveDayTicketModel(dayTicketModel: DayTicketModel){
        val ticketDTO = dayTicketModel.toDto()
        val json = json.encodeToString(ticketDTO)

        context.openFileOutput(dayTicketModel.id, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }

    }

    companion object JsonManager {
        private val json = Json {
            ignoreUnknownKeys = true
        }
    }
}
