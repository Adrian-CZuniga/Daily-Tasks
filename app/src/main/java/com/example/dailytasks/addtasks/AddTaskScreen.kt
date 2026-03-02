package com.example.dailytasks.addtasks

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytasks.core.domain.TaskSequenceLimitModel
import com.example.dailytasks.core.domain.TaskSingleModel
import com.example.dailytasks.core.domain.TypeTask
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTaskScreen(
    onNavigateToHomeScreen : () -> Unit = {}
){
    // ── Estado local de la pantalla ───────────────────────────────────────────
    // Cuando integres el ViewModel, reemplaza estos remember por:
    //   val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // y cada setter por la llamada correspondiente al ViewModel.

    var name      by remember { mutableStateOf("") }
    var taskMode  by remember { mutableStateOf(TaskFrequency.SINGLE) }
    var taskType  by remember { mutableStateOf(TypeTask.PERSONAL) }

    // Single
    var singleDate by remember { mutableStateOf<LocalDate?>(null) }
    var singleTime by remember { mutableStateOf<LocalTime?>(null) }

    // Recurring
    var schedule   by remember { mutableStateOf<Map<DayOfWeek, List<LocalTime>>>(emptyMap()) }
    var hasLimit   by remember { mutableStateOf(false) }
    var limitDate  by remember { mutableStateOf<LocalDate?>(null) }

    // Validación
    var errors     by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isSaving   by remember { mutableStateOf(false) }

    // ── Acent color animado al cambiar categoría ──────────────────────────────
    val accentColor by animateColorAsState(
        targetValue   = taskType.brandColor(),
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
                onNavigateUp = {
                    // ── LLAMAR AL VIEWMODEL / NAVEGACIÓN ─────────────────────
                    // navController.navigateUp()
                    // ─────────────────────────────────────────────────────────
                },
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
                    InputField(label = "Task Name", error = errors["name"]) {
                        OutlinedTextField(
                            value         = name,
                            onValueChange = { newValue ->
                                name = newValue

                                // ── LLAMAR AL VIEWMODEL ───────────────────────
                                // viewModel.onNameChange(newValue)
                                // ─────────────────────────────────────────────
                            },
                            placeholder = {
                                Text(
                                    "What needs to be done?",
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
                                unfocusedBorderColor    = if (errors["name"] != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor      = if (errors["name"] != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                                cursorColor             = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }

                    // ── Chips de categoría ────────────────────────────────────
                    InputField(label = "Category") {
                        FlowRow(
                            modifier              = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(8.dp),
                        ) {
                            TypeTask.entries.forEach { type ->
                                TypeTaskChip(
                                    taskType = type,
                                    selected = taskType == type,
                                    onClick  = {
                                        taskType = type

                                        // ── LLAMAR AL VIEWMODEL ───────────────
                                        // viewModel.onTypeChange(type)
                                        // ─────────────────────────────────────
                                    },
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
                            text  = "SCHEDULE TYPE",
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
                                label    = "📌  One-time",
                                selected = taskMode == TaskFrequency.SINGLE,
                                onClick  = {
                                    taskMode = TaskFrequency.SINGLE

                                    // ── LLAMAR AL VIEWMODEL ───────────────────
                                    // viewModel.onModeChange(TaskMode.SINGLE)
                                    // ─────────────────────────────────────────
                                },
                                modifier = Modifier.weight(1f),
                            )
                            ModeTab(
                                label    = "🔄  Recurring",
                                selected = taskMode == TaskFrequency.RECURRING,
                                onClick  = {
                                    taskMode = TaskFrequency.RECURRING

                                    // ── LLAMAR AL VIEWMODEL ───────────────────
                                    // viewModel.onModeChange(TaskMode.RECURRING)
                                    // ─────────────────────────────────────────
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    // ── Sección dinámica (animada) ────────────────────────────
                    AnimatedContent(
                        targetState = taskMode,
                        transitionSpec = {
                            (fadeIn(tween(250)) + slideInVertically { it / 8 })
                                .togetherWith(fadeOut(tween(150)) + slideOutVertically { -it / 8 })
                        },
                        label = "mode_section",
                    ) { mode ->
                        when (mode) {
                            TaskFrequency.SINGLE -> SingleTaskSection(
                                selectedDate = singleDate,
                                selectedTime = singleTime,
                                dateError    = errors["singleDate"],
                                timeError    = errors["singleTime"],
                            )
                            TaskFrequency.RECURRING -> RecurringTaskSection(
                                schedule       = schedule,
                                hasLimit       = hasLimit,
                                limitDate      = limitDate,
                                daysError      = errors["days"],
                                timesError     = errors["times"],
                                limitDateError = errors["limitDate"],
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Botones de acción ─────────────────────────────────────────────
            ActionButtons(
                isSaving    = isSaving,
                accentColor = accentColor,
                onDiscard   = {
                    // ── LLAMAR AL VIEWMODEL / NAVEGACIÓN ─────────────────────
                    // navController.navigateUp()
                    // ─────────────────────────────────────────────────────────
                },
                onSave = {
                    // ── LLAMAR AL VIEWMODEL ───────────────────────────────────
                    // viewModel.onSave()
                    // ─────────────────────────────────────────────────────────
                },
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}