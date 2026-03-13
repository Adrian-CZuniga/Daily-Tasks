package com.example.dailytasks.addtasks

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailytasks.R
import com.example.dailytasks.core.domain.TypeTask
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.example.dailytasks.core.domain.brandColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTaskScreen(
    onNavigateToHomeScreen: () -> Unit = {},
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateToHomeScreen()
        }
    }

    val accentColor by animateColorAsState(
        targetValue = uiState.taskType.brandColor(),
        animationSpec = tween(350),
        label = "accent",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // --- Header Integrado ---
        ScreenHeader(
            onNavigateUp = onNavigateToHomeScreen,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Línea de acento sutil debajo del header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(accentColor.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // --- Nombre de la Tarea (Más limpio) ---
            InputField(label = stringResource(R.string.task_name_label), error = uiState.errors["name"]) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    placeholder = {
                        Text(
                            stringResource(R.string.task_name_placeholder),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = accentColor,
                        cursorColor = accentColor,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    ),
                )
            }

            // --- Categorías (Chips Minimalistas) ---
            InputField(label = stringResource(R.string.category_label)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TypeTask.entries.forEach { type ->
                        TypeTaskChip(
                            taskType = type,
                            selected = uiState.taskType == type,
                            onClick = { viewModel.onTypeChange(type) },
                        )
                    }
                }
            }

            // --- Selector de Modo (Sin botones gigantes) ---
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.schedule_type_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    ModeTab(
                        label = stringResource(R.string.mode_one_time),
                        selected = uiState.taskMode == TaskMode.SINGLE,
                        onClick = { viewModel.onModeChange(TaskMode.SINGLE) },
                        modifier = Modifier.weight(1f),
                    )
                    ModeTab(
                        label = stringResource(R.string.mode_recurring),
                        selected = uiState.taskMode == TaskMode.RECURRING,
                        onClick = { viewModel.onModeChange(TaskMode.RECURRING) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // --- Secciones Dinámicas ---
            AnimatedContent(
                targetState = uiState.taskMode,
                transitionSpec = {
                    (fadeIn(tween(200)) + slideInVertically { it / 10 })
                        .togetherWith(fadeOut(tween(150)) + slideOutVertically { -it / 10 })
                },
                label = "mode_section",
            ) { mode ->
                when (mode) {
                    TaskMode.SINGLE -> SingleTaskSection(
                        selectedDate = uiState.singleDate,
                        selectedTime = uiState.singleTime,
                        dateError = uiState.errors["singleDate"],
                        timeError = uiState.errors["singleTime"],
                        onDateChange = { viewModel.onSingleDateChange(it) },
                        onTimeChange = { viewModel.onSingleTimeChange(it) }
                    )
                    TaskMode.RECURRING -> RecurringTaskSection(
                        schedule = uiState.schedule,
                        hasLimit = uiState.hasLimit,
                        limitDate = uiState.limitDate,
                        daysError = uiState.errors["days"],
                        timesError = uiState.errors["times"],
                        limitDateError = uiState.errors["limitDate"],
                        onToggleDay = { viewModel.onToggleDay(it) },
                        onAddTime = { day, time -> viewModel.onAddTime(day, time) },
                        onRemoveTime = { day, time -> viewModel.onRemoveTime(day, time) },
                        onToggleLimit = { viewModel.onToggleLimit() },
                        onLimitDateChange = { viewModel.onLimitDateChange(it) }
                    )
                }
            }
        }

        // --- Botones de Acción (Fijos al fondo, sin tarjeta) ---
        ActionButtons(
            isSaving = uiState.isSaving,
            accentColor = accentColor,
            onDiscard = onNavigateToHomeScreen,
            onSave = { viewModel.saveTask() },
            modifier = Modifier
                .padding(20.dp)
        )
    }
}