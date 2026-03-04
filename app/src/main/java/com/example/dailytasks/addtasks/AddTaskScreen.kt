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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTaskScreen(
    onNavigateToHomeScreen : () -> Unit = {},
    viewModel: AddTaskViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ── Navegar al inicio cuando se guarde con éxito ─────────────────────────
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateToHomeScreen()
        }
    }

    // ── Accent color animado al cambiar categoría ──────────────────────────────
    val accentColor by animateColorAsState(
        targetValue   = uiState.taskType.brandColor(),
        animationSpec = tween(350),
        label         = "accent",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Header ────────────────────────────────────────────────────────
            ScreenHeader(
                onNavigateUp = onNavigateToHomeScreen,
            )

            Spacer(Modifier.height(26.dp))

            // ── Card principal ────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation    = 24.dp,
                        shape        = RoundedCornerShape(22.dp),
                        ambientColor = Color.Black.copy(alpha = .6f),
                        spotColor    = Color.Black.copy(alpha = .4f),
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
            ) {
                AccentBar(categoryColor = accentColor)

                Column(
                    modifier            = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {

                    // ── Nombre de la tarea ────────────────────────────────────
                    InputField(label = stringResource(R.string.task_name_label), error = uiState.errors["name"]) {
                        OutlinedTextField(
                            value         = uiState.name,
                            onValueChange = { viewModel.onNameChange(it) },
                            placeholder = {
                                Text(
                                    stringResource(R.string.task_name_placeholder),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color      = MaterialTheme.colorScheme.onSurface,
                            ),
                            singleLine      = true,
                            modifier        = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction      = ImeAction.Next,
                            ),
                            shape  = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor    = if (uiState.errors["name"] != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor      = if (uiState.errors["name"] != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                                cursorColor             = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }

                    // ── Chips de categoría ────────────────────────────────────
                    InputField(label = stringResource(R.string.category_label)) {
                        FlowRow(
                            modifier              = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(8.dp),
                        ) {
                            TypeTask.entries.forEach { type ->
                                TypeTaskChip(
                                    taskType = type,
                                    selected = uiState.taskType == type,
                                    onClick  = { viewModel.onTypeChange(type) },
                                )
                            }
                        }
                    }

                    // ── Divisor ───────────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant),
                    )

                    // ── Segmented control de modo ─────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text  = stringResource(R.string.schedule_type_label),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight    = FontWeight.Bold,
                                letterSpacing = 0.7.sp,
                                color         = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            ModeTab(
                                label    = stringResource(R.string.mode_one_time),
                                selected = uiState.taskMode == TaskMode.SINGLE,
                                onClick  = { viewModel.onModeChange(TaskMode.SINGLE) },
                                modifier = Modifier.weight(1f),
                            )
                            ModeTab(
                                label    = stringResource(R.string.mode_recurring),
                                selected = uiState.taskMode == TaskMode.RECURRING,
                                onClick  = { viewModel.onModeChange(TaskMode.RECURRING) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    // ── Sección dinámica (animada) ────────────────────────────
                    AnimatedContent(
                        targetState = uiState.taskMode,
                        transitionSpec = {
                            (fadeIn(tween(250)) + slideInVertically { it / 8 })
                                .togetherWith(fadeOut(tween(150)) + slideOutVertically { -it / 8 })
                        },
                        label = "mode_section",
                    ) { mode ->
                        when (mode) {
                            TaskMode.SINGLE -> SingleTaskSection(
                                selectedDate = uiState.singleDate,
                                selectedTime = uiState.singleTime,
                                dateError    = uiState.errors["singleDate"],
                                timeError    = uiState.errors["singleTime"],
                                onDateChange = { viewModel.onSingleDateChange(it) },
                                onTimeChange = { viewModel.onSingleTimeChange(it) }
                            )
                            TaskMode.RECURRING -> RecurringTaskSection(
                                schedule       = uiState.schedule,
                                hasLimit       = uiState.hasLimit,
                                limitDate      = uiState.limitDate,
                                daysError      = uiState.errors["days"],
                                timesError     = uiState.errors["times"],
                                limitDateError = uiState.errors["limitDate"],
                                onToggleDay    = { viewModel.onToggleDay(it) },
                                onAddTime      = { day, time -> viewModel.onAddTime(day, time) },
                                onRemoveTime   = { day, time -> viewModel.onRemoveTime(day, time) },
                                onToggleLimit  = { viewModel.onToggleLimit() },
                                onLimitDateChange = { viewModel.onLimitDateChange(it) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Botones de acción ─────────────────────────────────────────────
            ActionButtons(
                isSaving    = uiState.isSaving,
                accentColor = accentColor,
                onDiscard   = onNavigateToHomeScreen,
                onSave      = { viewModel.saveTask() },
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}