package com.example.dailytasks.addtasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTaskSection(
    schedule: Map<DayOfWeek, List<LocalTime>>,
    hasLimit: Boolean,
    limitDate: LocalDate?,
    daysError: String?,
    timesError: String?,
    limitDateError: String?,
    onToggleDay: (DayOfWeek) -> Unit,
    onAddTime: (DayOfWeek, LocalTime) -> Unit,
    onRemoveTime: (DayOfWeek, LocalTime) -> Unit,
    onToggleLimit: () -> Unit,
    onLimitDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var timePickerTargetDay by remember { mutableStateOf<DayOfWeek?>(null) }
    var showLimitDatePicker  by remember { mutableStateOf(false) }

    val orderedDays    = remember { DayOfWeek.entries }
    val limitFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }

    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        InputField(label = stringResource(R.string.active_days_label), error = daysError) {
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                orderedDays.forEach { day ->
                    val selected = schedule.containsKey(day)
                    DayChip(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape,
                            )
                            .clickable(onClick = {
                                onToggleDay(day)
                            }),
                        label    = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2),
                        selected = schedule.containsKey(day),
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = schedule.isNotEmpty(),
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut(),
        ) {
            InputField(label = stringResource(R.string.time_slots_label), error = timesError) {
                Column(
                    modifier            = Modifier.padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    orderedDays
                        .filter { schedule.containsKey(it) }
                        .forEach { day ->
                            DayTimeRow(
                                modifier = Modifier.fillMaxWidth(),
                                day = day,
                                times = schedule[day].orEmpty(),
                                onPickTime = { timePickerTargetDay = day },
                                onRemoveTime = { time -> onRemoveTime(day, time) },
                            )
                        }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsToggle(
                label           = stringResource(R.string.set_end_date_label),
                hint            = stringResource(R.string.set_end_date_hint),
                checked         = hasLimit,
                onCheckedChange = { onToggleLimit() },
            )
            AnimatedVisibility(
                visible = hasLimit,
                enter   = expandVertically() + fadeIn(),
                exit    = shrinkVertically() + fadeOut(),
            ) {
                InputField(label = stringResource(R.string.until_label), error = limitDateError) {
                    PickerButton(
                        text          = limitDate?.format(limitFormatter) ?: stringResource(R.string.select_end_date),
                        isPlaceholder = limitDate == null,
                        hasError      = limitDateError != null,
                        leadingEmoji  = "🗓",
                        onClick       = { showLimitDatePicker = true },
                    )
                }
            }
        }
    }

    timePickerTargetDay?.let { targetDay ->
        val pickerState = rememberTimePickerState(is24Hour = true)
        Dialog(onDismissRequest = { timePickerTargetDay = null }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text  = "${stringResource(R.string.add_button)} · ${targetDay.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                    style = MaterialTheme.typography.titleMedium,
                )
                TimePicker(state = pickerState)
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { timePickerTargetDay = null }) { Text(stringResource(R.string.cancel_button)) }
                    TextButton(onClick = {
                        val time = LocalTime.of(pickerState.hour, pickerState.minute)
                        onAddTime(targetDay, time)
                        timePickerTargetDay = null
                    }) { Text(stringResource(R.string.add_button)) }
                }
            }
        }
    }

    if (showLimitDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = limitDate
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
                ?: System.currentTimeMillis(),
        )
        DatePickerDialog(
            onDismissRequest = { showLimitDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onLimitDateChange(date)
                    }
                    showLimitDatePicker = false
                }) { Text(stringResource(R.string.ok_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showLimitDatePicker = false }) { Text(stringResource(R.string.cancel_button)) }
            },
        ) { DatePicker(state = pickerState) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DayTimeRow(
    modifier: Modifier = Modifier,
    day: DayOfWeek,
    times: List<LocalTime>,
    onPickTime: () -> Unit,
    onRemoveTime: (LocalTime) -> Unit,
) {
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text  = day.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                ),
            )
            Text(
                text  = "${times.size} slot${if (times.size != 1) "s" else ""}",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            )
        }

        if (times.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement   = Arrangement.spacedBy(6.dp),
            ) {
                times.forEach { time ->
                    TimeSlotChip(
                        displayTime = time.format(formatter),
                        onRemove    = { onRemoveTime(time) },
                    )
                }
            }
        }

        TextButton(
            onClick  = onPickTime,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = .12f)),
        ) {
            Text(
                text  = stringResource(R.string.add_time_button),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}