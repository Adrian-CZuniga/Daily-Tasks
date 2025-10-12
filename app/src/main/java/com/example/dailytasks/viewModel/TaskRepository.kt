package com.example.dailytasks.viewModel

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dailytasks.model.DayTicketModel
import com.example.dailytasks.model.TaskModel
import com.example.dailytasks.model.TaskSequenceLimitModel
import com.example.dailytasks.pagination.FileTicketsPagingSource
import kotlinx.coroutines.flow.Flow
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TaskRepository(private val context: Context) {

    fun getPagedDayTicketsFromDay(day : LocalDate) : Flow<PagingData<DayTicketModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FileTicketsPagingSource(context,  fromDate = day, 20) }
        ).flow
    }

    suspend fun saveTask(task : TaskModel){
    }
}