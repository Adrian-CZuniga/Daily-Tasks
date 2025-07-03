package com.example.dailytasks.viewModel

import androidx.lifecycle.ViewModel
import com.example.dailytasks.model.DayTicketModel
import com.example.dailytasks.model.TaskSequenceLimitModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor() : ViewModel() {
    val taskRepository = TaskRepository()

    fun getTicketsByDate(date : LocalDate) : List<DayTicketModel> {
        return taskRepository.getTicketsByDate(date)
    }

    fun getTaskSequenceLimitModel() : TaskSequenceLimitModel {
        return taskRepository.getTaskSequenceLimitModel()
    }


}