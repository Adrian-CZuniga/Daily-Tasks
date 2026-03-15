package com.example.dailytasks.addtasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dailytasks.R
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTaskSection(
    schedule: Map<DayOfWeek, List<LocalTime>>,
    hasLimit: Boolean,
    limitDate: LocalDate?,
    daysError: String?,
    timesError: String?,
    limitDateError: String?,
    onToggleDayForTime: (DayOfWeek, LocalTime) -> Unit,
    onAddTimeBlock: (LocalTime, List<DayOfWeek>) -> Unit,
    onRemoveTimeBlock: (LocalTime) -> Unit,
    onToggleLimit: () -> Unit,
    onLimitDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var showLimitDatePicker by remember { mutableStateOf(false) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val limitFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }

    val timeToDays = remember(schedule) {
        val map = mutableMapOf<LocalTime, MutableList<DayOfWeek>>()
        schedule.forEach { (day, times) ->
            times.forEach { time ->
                map.getOrPut(time) { mutableListOf() }.add(day)
            }
        }
        map.toSortedMap()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        InputField(
            label = stringResource(R.string.time_slots_label),
            error = timesError ?: daysError
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                timeToDays.forEach { (time, activeDays) ->
                    TimeSlotBlock(
                        time = time,
                        formattedTime = time.format(timeFormatter),
                        activeDays = activeDays,
                        onToggleDay = { day -> onToggleDayForTime(day, time) },
                        onRemoveBlock = { onRemoveTimeBlock(time) }
                    )
                }

                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add_time_button), fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- Fecha Límite ---
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SettingsToggle(
                label = stringResource(R.string.set_end_date_label),
                hint = stringResource(R.string.set_end_date_hint),
                checked = hasLimit,
                onCheckedChange = onToggleLimit
            )

            AnimatedVisibility(visible = hasLimit, enter = expandVertically(), exit = shrinkVertically()) {
                InputField(label = stringResource(R.string.until_label), error = limitDateError) {
                    PickerButton(
                        text = limitDate?.format(limitFormatter) ?: stringResource(R.string.select_end_date),
                        isPlaceholder = limitDate == null,
                        hasError = limitDateError != null,
                        leadingIcon = Icons.Rounded.CalendarToday,
                        onClick = { showLimitDatePicker = true }
                    )
                }
            }
        }
    }

    // --- DIÁLOGO DE NUEVO HORARIO + DÍAS ---
    if (showTimePicker) {
        var selectedDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
        val pickerState = rememberTimePickerState(is24Hour = true)

        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.select_time), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(20.dp))
                    
                    TimePicker(state = pickerState)
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        text = stringResource(R.string.select_active_days),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        DayOfWeek.entries.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            DayChip(
                                modifier = Modifier.weight(1f),
                                label = stringResource(day.getStringRes()),
                                selected = isSelected,
                                onClick = {
                                    selectedDays = if (isSelected) selectedDays - day else selectedDays + day
                                }
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(stringResource(R.string.cancel_button))
                        }
                        Button(
                            onClick = {
                                onAddTimeBlock(
                                    LocalTime.of(pickerState.hour, pickerState.minute),
                                    selectedDays.toList()
                                )
                                showTimePicker = false
                            },
                            enabled = selectedDays.isNotEmpty(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(R.string.add_button))
                        }
                    }
                }
            }
        }
    }

    if (showLimitDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = limitDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
                ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showLimitDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onLimitDateChange(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate())
                    }
                    showLimitDatePicker = false
                }) { Text(stringResource(R.string.ok_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showLimitDatePicker = false }) { Text(stringResource(R.string.cancel_button)) }
            }
        ) { DatePicker(state = pickerState) }
    }
}

@Composable
fun TimeSlotBlock(
    time: LocalTime,
    formattedTime: String,
    activeDays: List<DayOfWeek>,
    onToggleDay: (DayOfWeek) -> Unit,
    onRemoveBlock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = formattedTime,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onRemoveBlock) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DayOfWeek.entries.forEach { day ->
                DayChip(
                    label = stringResource(day.getStringRes()),
                    selected = activeDays.contains(day),
                    onClick = { onToggleDay(day) }
                )
            }
        }
    }
}

fun DayOfWeek.getStringRes(): Int {
    return when (this) {
        DayOfWeek.MONDAY -> R.string.day_monday
        DayOfWeek.TUESDAY -> R.string.day_tuesday
        DayOfWeek.WEDNESDAY -> R.string.day_wednesday
        DayOfWeek.THURSDAY -> R.string.day_thursday
        DayOfWeek.FRIDAY -> R.string.day_friday
        DayOfWeek.SATURDAY -> R.string.day_saturday
        DayOfWeek.SUNDAY -> R.string.day_sunday
    }
}
