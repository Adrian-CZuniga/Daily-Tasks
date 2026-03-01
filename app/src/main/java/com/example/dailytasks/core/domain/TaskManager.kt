package com.example.dailytasks.core.domain

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dailytasks.core.utils.FileTicketsPagingSource
import kotlinx.coroutines.flow.Flow
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
        }
    }

    private fun saveTaskSequenceLimitModel(task: TaskSequenceLimitModel) {
        // Lógica para guardar una tarea de secuencia limitada
    }

    private fun saveTaskSingleModel(task: TaskSingleModel) {
        // Lógica para guardar una tarea única
    }
}