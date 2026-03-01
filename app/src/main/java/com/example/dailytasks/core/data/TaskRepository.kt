package com.example.dailytasks.core.data

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dailytasks.core.domain.DayTicketModel
import com.example.dailytasks.core.domain.TaskModel
import com.example.dailytasks.core.utils.FileTicketsPagingSource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskRepository(private val context: Context) {

    fun getPagedDayTicketsFromDay(day : LocalDate) : Flow<PagingData<DayTicketModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FileTicketsPagingSource(context, fromDate = day, 20) }
        ).flow
    }

    suspend fun saveTask(task : TaskModel){
    }
}