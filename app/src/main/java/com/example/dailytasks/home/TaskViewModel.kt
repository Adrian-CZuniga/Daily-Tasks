package com.example.dailytasks.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.core.data.ITaskRepository
import com.example.dailytasks.core.domain.DailyTaskModel
import com.example.dailytasks.core.domain.Status
import com.example.dailytasks.core.domain.TaskModel
import com.example.dailytasks.core.domain.TaskSequenceLimitModel
import com.example.dailytasks.core.domain.TaskSingleModel
import com.example.dailytasks.core.domain.TypeTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository : ITaskRepository
) : ViewModel() {
    var status : MutableStateFlow<Status> = MutableStateFlow(Status.UNDEFINED)
    private val _selectedDay : MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val selectedDay = _selectedDay.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    var dayTickets : StateFlow<List<DailyTaskModel>> = selectedDay
        .flatMapLatest {
            taskRepository.getDailyTasks(it)
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )



    fun getTaskSingleModel() : TaskSingleModel {
        return TaskSingleModel(
            name = "Test",
            date = LocalDate.now(),
            time = LocalDate.now().atTime(10, 0)
                .toLocalTime(),
            id = TaskModel.createId(name = "Test tarea single", type = TypeTask.PERSONAL)
        )
    }

    fun getTaskSequenceLimitModel() : TaskSequenceLimitModel {
        return TaskSequenceLimitModel(
            name = "test tarea secuencial",
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
            id = TaskModel.createId(name = "Test", type = TypeTask.PERSONAL)
        )
    }

    fun changeDay(newDate: LocalDate) {
        _selectedDay.value = newDate
    }
    fun saveTask(
        taskModel: TaskModel
    ){
        viewModelScope.launch {
            taskRepository.saveTask(taskModel)
        }
    }
}