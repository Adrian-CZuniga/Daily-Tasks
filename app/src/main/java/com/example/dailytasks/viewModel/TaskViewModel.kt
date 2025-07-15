package com.example.dailytasks.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.model.DayTicketModel
import com.example.dailytasks.model.TaskSequenceLimitModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(context: Context) : ViewModel() {
    private val taskRepository = TaskRepository(context)
    var status : MutableStateFlow<Status> = MutableStateFlow(Status.UNDEFINED)

    var dayTickets : MutableStateFlow<List<DayTicketModel>> = MutableStateFlow(listOf())
        private set

    init {
        status.value = Status.LOADING
        getActualDayTickets()
    }

    fun getActualDayTickets() {
        viewModelScope.launch {
            dayTickets.value = getTicketsByDate(LocalDate.now())
            status.value = Status.SUCCESS
        }
    }

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