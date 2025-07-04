package com.example.dailytasks.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.dailytasks.model.DayTicketModel
import com.example.dailytasks.model.TaskSequenceLimitModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(context: Context) : ViewModel() {
    val taskRepository = TaskRepository(context)

    fun getPagedTickets(fromDate: LocalDate) = taskRepository.getPagedTickets(fromDate)

    fun getTicketsByRangeDate(fromDate : LocalDate, toDate : LocalDate) : List<DayTicketModel> {
        return taskRepository.getTicketsByRangeDate(fromDate, toDate)
    }

    fun getTicketsByDate(date : LocalDate) : List<DayTicketModel> {
        return taskRepository.getTaskSingleModel(date)
    }

    fun getTaskSequenceLimitModel() : TaskSequenceLimitModel {
        return taskRepository.getTaskSequenceLimitModel()
    }


}