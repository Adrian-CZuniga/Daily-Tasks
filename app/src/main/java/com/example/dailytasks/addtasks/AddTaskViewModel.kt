package com.example.dailytasks.addtasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytasks.core.data.ITaskRepository
import com.example.dailytasks.core.domain.TaskModel
import com.example.dailytasks.core.domain.TaskSequenceLimitModel
import com.example.dailytasks.core.domain.TaskSingleModel
import com.example.dailytasks.core.domain.TypeTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

enum class TaskMode { SINGLE, RECURRING }

data class AddTaskUiState(
    val name: String = "",
    val taskMode: TaskMode = TaskMode.SINGLE,
    val taskType: TypeTask = TypeTask.PERSONAL,
    // Single
    val singleDate: LocalDate? = null,
    val singleTime: LocalTime? = null,
    // Recurring
    val schedule: Map<DayOfWeek, List<LocalTime>> = emptyMap(),
    val hasLimit: Boolean = false,
    val limitDate: LocalDate? = null,
    // Status
    val errors: Map<String, String> = emptyMap(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repository: ITaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName, errors = it.errors - "name") }
    }

    fun onModeChange(newMode: TaskMode) {
        _uiState.update { it.copy(taskMode = newMode) }
    }

    fun onTypeChange(newType: TypeTask) {
        _uiState.update { it.copy(taskType = newType) }
    }

    fun onSingleDateChange(date: LocalDate) {
        _uiState.update { it.copy(singleDate = date, errors = it.errors - "singleDate") }
    }

    fun onSingleTimeChange(time: LocalTime) {
        _uiState.update { it.copy(singleTime = time, errors = it.errors - "singleTime") }
    }

    fun onToggleDay(day: DayOfWeek) {
        _uiState.update { state ->
            val newSchedule = state.schedule.toMutableMap()
            if (newSchedule.containsKey(day)) {
                newSchedule.remove(day)
            } else {
                newSchedule[day] = emptyList()
            }
            state.copy(schedule = newSchedule, errors = state.errors - "days")
        }
    }

    fun onAddTime(day: DayOfWeek, time: LocalTime) {
        _uiState.update { state ->
            val newSchedule = state.schedule.toMutableMap()
            val dayTimes = newSchedule[day]?.toMutableList() ?: mutableListOf()
            if (!dayTimes.contains(time)) {
                dayTimes.add(time)
                dayTimes.sort()
            }
            newSchedule[day] = dayTimes
            state.copy(schedule = newSchedule, errors = state.errors - "times")
        }
    }

    fun onRemoveTime(day: DayOfWeek, time: LocalTime) {
        _uiState.update { state ->
            val newSchedule = state.schedule.toMutableMap()
            val dayTimes = newSchedule[day]?.toMutableList() ?: return@update state
            dayTimes.remove(time)
            newSchedule[day] = dayTimes
            state.copy(schedule = newSchedule)
        }
    }

    fun onToggleLimit() {
        _uiState.update { it.copy(hasLimit = !it.hasLimit) }
    }

    fun onLimitDateChange(date: LocalDate) {
        _uiState.update { it.copy(limitDate = date, errors = it.errors - "limitDate") }
    }

    fun saveTask() {
        val state = _uiState.value
        val newErrors = mutableMapOf<String, String>()

        if (state.name.isBlank()) newErrors["name"] = "Name cannot be empty"

        val task: TaskModel? = when (state.taskMode) {
            TaskMode.SINGLE -> {
                if (state.singleDate == null) newErrors["singleDate"] = "Select a date"
                if (state.singleTime == null) newErrors["singleTime"] = "Select a time"
                
                if (newErrors.isEmpty()) {
                    TaskSingleModel(
                        name = state.name,
                        date = state.singleDate!!,
                        time = state.singleTime!!,
                        type = state.taskType,
                        id = UUID.randomUUID().toString()
                    )
                } else null
            }
            TaskMode.RECURRING -> {
                if (state.schedule.isEmpty()) newErrors["days"] = "Select at least one day"
                if (state.schedule.values.all { it.isEmpty() }) newErrors["times"] = "Add at least one time slot"
                if (state.hasLimit && state.limitDate == null) newErrors["limitDate"] = "Select a limit date"

                if (newErrors.isEmpty()) {
                    TaskSequenceLimitModel(
                        name = state.name,
                        schedule = state.schedule,
                        limitDate = if (state.hasLimit) state.limitDate else null,
                        type = state.taskType,
                        id = UUID.randomUUID().toString()
                    )
                } else null
            }
        }

        if (newErrors.isNotEmpty()) {
            _uiState.update { it.copy(errors = newErrors) }
            return
        }

        task?.let {
            viewModelScope.launch {
                _uiState.update { it -> it.copy(isSaving = true) }
                repository.saveTask(it)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            }
        }
    }
}