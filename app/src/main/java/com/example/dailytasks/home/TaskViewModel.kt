package com.example.dailytasks.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.core.data.ITaskRepository
import com.example.dailytasks.core.domain.TicketModel
import com.example.dailytasks.core.domain.Status
import com.example.dailytasks.core.domain.TaskModel
import com.example.dailytasks.core.domain.TaskSequenceLimitModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository : ITaskRepository
) : ViewModel() {
    var status : MutableStateFlow<Status> = MutableStateFlow(Status.UNDEFINED)

    var dayTickets : MutableStateFlow<List<TicketModel>> = MutableStateFlow(listOf())
        private set

    init {
        status.value = Status.LOADING
        getActualDayTickets()
    }

    private fun getActualDayTickets() {
        viewModelScope.launch {
            dayTickets.value = getTicketsByDate(LocalDate.now())
            status.value = Status.SUCCESS
        }
    }


    fun getTicketsByDate(date : LocalDate) : List<TicketModel> {
        return listOf()
    }

    fun getTaskSequenceLimitModel() : TaskSequenceLimitModel {
        return TaskSequenceLimitModel(
            name = "Test",
            schedule = mapOf(
                DayOfWeek.MONDAY to listOf(
                    LocalDate.now().atTime(10, 0).toLocalTime(),
                    LocalDate.now().atTime(15, 0).toLocalTime()
                ),
                DayOfWeek.WEDNESDAY to listOf(
                    LocalDate.now().atTime(10, 0).toLocalTime()
                ),
                DayOfWeek.FRIDAY to listOf(
                    LocalDate.now().atTime(10, 0).toLocalTime(),
                    LocalDate.now().atTime(15, 0).toLocalTime()
                )
            ),
            limitDate = LocalDate.now().plusDays(30),
            id = "test_id_1"
        )
    }

    fun saveTask(
        taskModel: TaskModel
    ){
        viewModelScope.launch {
            taskRepository.saveTask(taskModel)
        }
    }
}