package com.example.dailytasks.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.model.DayTicketModel
import com.example.dailytasks.model.TaskSequenceLimitModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import kotlin.to

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository : TaskRepository
) : ViewModel() {
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


    fun getTicketsByDate(date : LocalDate) : List<DayTicketModel> {
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
}