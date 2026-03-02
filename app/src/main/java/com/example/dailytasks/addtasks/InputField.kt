package com.example.dailytasks.addtasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytasks.core.domain.TypeTask

// ─────────────────────────────────────────────────────────────────────────────
//  InputField  –  label + content slot + animated error message
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun InputField(
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight    = FontWeight.Bold,
                letterSpacing = 0.7.sp,
                color         = MaterialTheme.colorScheme.background,
            ),
        )
        content()
        AnimatedVisibility(
            visible = error != null,
            enter   = expandVertically() + fadeIn(),
            exit    = shrinkVertically() + fadeOut(),
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("⚠", fontSize = 11.sp)
                Text(
                    text  = error.orEmpty(),
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  TypeTaskChip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TypeTaskChip(
    taskType: TypeTask,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val chipColor = taskType.brandColor()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) chipColor.copy(alpha = .13f) else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 2.dp,
                color = if (selected) chipColor else Color.Transparent,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 7.dp),
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(chipColor, CircleShape),
            )
            Text(
                text  = taskType.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color      = if (selected) chipColor else MaterialTheme.colorScheme.surfaceVariant,
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  DayChip  –  selector circular por día de la semana
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun DayChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = if (selected)  MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  SettingsToggle  –  switch animado con label y hint
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SettingsToggle(
    label: String,
    hint: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = hint,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            )
        }
        Spacer(Modifier.width(16.dp))
        val thumbX by animateDpAsState(
            targetValue   = if (checked) 23.dp else 3.dp,
            animationSpec = tween(200),
            label         = "toggle_thumb",
        )
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onCheckedChange),
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbX, y = 3.dp)
                    .size(20.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  AccentBar  –  barra de gradiente superior de la card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AccentBar(
    categoryColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(
                Brush.horizontalGradient(listOf(categoryColor, MaterialTheme.colorScheme.primary)),
            ),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
//  ModeTab  –  pestaña del segmented control One-time / Recurring
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ModeTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color      = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  TimeSlotChip  –  chip de horario con botón de eliminar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TimeSlotChip(
    displayTime: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(start = 10.dp, end = 4.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text("🕐", fontSize = 11.sp)
        Text(
            text  = displayTime,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.width(2.dp))
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = "×",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.error,
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  PickerButton  –  campo tappable para date/time pickers
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun PickerButton(
    text: String,
    isPlaceholder: Boolean,
    hasError: Boolean,
    leadingEmoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.5.dp,
                color = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(leadingEmoji, fontSize = 15.sp)
        Text(
            text  = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isPlaceholder) FontWeight.Normal else FontWeight.SemiBold,
                color      = if (isPlaceholder) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            ),
            maxLines = 1,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Extension: color de marca por TypeTask
// ─────────────────────────────────────────────────────────────────────────────
fun TypeTask.brandColor(): Color = when (this) {
    TypeTask.PERSONAL -> Color.Blue
    TypeTask.STUDY    -> Color.Yellow
    TypeTask.WORK     -> Color.Red
    TypeTask.HEALTH   -> Color.Green
    TypeTask.OTHER    -> Color.Magenta
}