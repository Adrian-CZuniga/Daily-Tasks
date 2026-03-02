package com.example.dailytasks.addtasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleTaskSection(
    // Valores actuales leídos del ViewModel (o null si aún no se han elegido)
    selectedDate: LocalDate?,
    selectedTime: LocalTime?,
    dateError: String?,
    timeError: String?,
    modifier: Modifier = Modifier,
) {
    // ── Estado de visibilidad de diálogos ─────────────────────────────────────
    // Estos son puramente estado de UI; no tienen valor de negocio.
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Column(
        modifier            = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {

        // ── Fila fecha / hora ─────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            InputField(
                label    = "Date",
                error    = dateError,
                modifier = Modifier.weight(1f),
            ) {
                PickerButton(
                    text          = selectedDate?.format(dateFormatter) ?: "Select date",
                    isPlaceholder = selectedDate == null,
                    hasError      = dateError != null,
                    leadingEmoji  = "📅",
                    onClick       = { showDatePicker = true },
                )
            }
            InputField(
                label    = "Time",
                error    = timeError,
                modifier = Modifier.weight(1f),
            ) {
                PickerButton(
                    text          = selectedTime?.format(timeFormatter) ?: "Select time",
                    isPlaceholder = selectedTime == null,
                    hasError      = timeError != null,
                    leadingEmoji  = "🕐",
                    onClick       = { showTimePicker = true },
                )
            }
        }

        // ── Pill resumen ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("📅", fontSize = 18.sp)
            Text(
                text = when {
                    selectedDate != null && selectedTime != null ->
                        "Scheduled · ${selectedDate.format(dateFormatter)} at ${selectedTime.format(timeFormatter)}"
                    else -> "Pick a date and time above"
                },
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            )
        }
    }

    // ── DatePickerDialog ──────────────────────────────────────────────────────
    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
                ?: System.currentTimeMillis(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        // ── LLAMAR AL VIEWMODEL ───────────────────────────────
                        // viewModel.onSingleDateChange(date)
                        // ─────────────────────────────────────────────────────
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }

    // ── TimePickerDialog ──────────────────────────────────────────────────────
    if (showTimePicker) {
        val initial     = selectedTime ?: LocalTime.now()
        val pickerState = rememberTimePickerState(
            initialHour   = initial.hour,
            initialMinute = initial.minute,
            is24Hour      = true,
        )
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Select time", style = MaterialTheme.typography.titleMedium)
                TimePicker(state = pickerState)
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                    TextButton(onClick = {
                        val time = LocalTime.of(pickerState.hour, pickerState.minute)

                        // ── LLAMAR AL VIEWMODEL ───────────────────────────────
                        // viewModel.onSingleTimeChange(time)
                        // ─────────────────────────────────────────────────────

                        showTimePicker = false
                    }) { Text("OK") }
                }
            }
        }
    }
}