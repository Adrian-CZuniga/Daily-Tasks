package com.example.dailytasks.core.ui.composables

import android.text.format.DateFormat
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytasks.core.domain.DailyTaskModel
import com.example.dailytasks.core.domain.TaskStatus
import com.example.dailytasks.core.domain.brandColor
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TicketListItem(
    dailyTaskModel: DailyTaskModel,
    onToggleComplete: (String) -> Unit,
    onEdit: (String) -> Unit = {},
    onCancel: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val is24Hour = DateFormat.is24HourFormat(context)
    val pattern = if (is24Hour) "HH:mm" else "hh:mm a"
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())

    val status = dailyTaskModel.status
    val isCompleted = status == TaskStatus.COMPLETED
    val isCancelled = status == TaskStatus.CANCELLED
    val isOverdue = status == TaskStatus.OVERDUE
    
    var showMenu by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isCompleted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            isCancelled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            isOverdue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(400),
        label = "backgroundColor"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (isCompleted || isCancelled) 0.6f else 1f,
        animationSpec = tween(400),
        label = "contentAlpha"
    )

    val borderColor = when {
        isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        isCancelled -> Color.Transparent
        isOverdue -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status Indicator Left
        StatusIcon(
            status = status,
            onClick = { if (!isCancelled) onToggleComplete(dailyTaskModel.ticketId) }
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Vertical Category Indicator
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    if (isCancelled) MaterialTheme.colorScheme.outlineVariant 
                    else dailyTaskModel.type.brandColor()
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Task Content
        Column(
            modifier = Modifier
                .weight(1f)
                .graphicsLayer { alpha = contentAlpha }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = dailyTaskModel.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isCompleted || isCancelled) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = when {
                        isOverdue -> MaterialTheme.colorScheme.error
                        isCancelled -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                
                if (isOverdue || isCancelled) {
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = status)
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = dailyTaskModel.time.format(formatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Box(
                    modifier = Modifier
                        .background(
                            color = (if (isCancelled) MaterialTheme.colorScheme.outlineVariant else dailyTaskModel.type.brandColor()).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = dailyTaskModel.type.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (isCancelled) MaterialTheme.colorScheme.onSurfaceVariant else dailyTaskModel.type.brandColor()
                    )
                }
            }
        }

        Box {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        showMenu = false
                        onEdit(dailyTaskModel.ticketId)
                    },
                    enabled = !isCompleted && !isCancelled
                )
                DropdownMenuItem(
                    text = { Text(if (isCancelled) "Restore" else "Cancel") },
                    onClick = {
                        showMenu = false
                        onCancel(dailyTaskModel.ticketId)
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusIcon(
    status: TaskStatus,
    onClick: () -> Unit
) {
    val isCompleted = status == TaskStatus.COMPLETED
    val isCancelled = status == TaskStatus.CANCELLED
    val isOverdue = status == TaskStatus.OVERDUE

    val containerColor = when {
        isCompleted -> MaterialTheme.colorScheme.primary
        isCancelled -> MaterialTheme.colorScheme.surfaceVariant
        isOverdue -> MaterialTheme.colorScheme.errorContainer
        else -> Color.Transparent
    }

    val iconColor = when {
        isCompleted -> MaterialTheme.colorScheme.onPrimary
        isCancelled -> MaterialTheme.colorScheme.onSurfaceVariant
        isOverdue -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(containerColor)
            .border(
                width = 2.dp,
                color = if (isCompleted || isCancelled || isOverdue) containerColor else MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
            .clickable(enabled = !isCancelled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            isCompleted -> Icon(Icons.Default.Check, null, Modifier.size(18.dp), iconColor)
            isCancelled -> Icon(Icons.Default.Block, null, Modifier.size(16.dp), iconColor)
            isOverdue -> Icon(Icons.Default.ErrorOutline, null, Modifier.size(18.dp), iconColor)
        }
    }
}

@Composable
private fun StatusBadge(status: TaskStatus) {
    val (text, color) = when (status) {
        TaskStatus.OVERDUE -> "OVERDUE" to MaterialTheme.colorScheme.error
        TaskStatus.CANCELLED -> "CANCELLED" to MaterialTheme.colorScheme.onSurfaceVariant
        else -> return
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}
