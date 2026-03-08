package com.example.dailytasks.core.ui.composables

import android.text.format.DateFormat
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.dailytasks.core.domain.DailyTaskModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TicketListItem(
    modifier: Modifier = Modifier,
    dailyTaskModel: DailyTaskModel,
    onToggleComplete: (String) -> Unit

){
    val context = LocalContext.current
    val is24Hour = DateFormat.is24HourFormat(context)

    // Define el patrón según configuración del sistema
    val pattern = if (is24Hour) "HH:mm" else "hh:mm a"

    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())

    val borderColor by animateColorAsState(
        targetValue = if (dailyTaskModel.isCompleted) Color(0xFF4CAF50) else Color.Transparent,
        animationSpec = tween(300),
        label = "border"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (dailyTaskModel.isCompleted) Color(0xFFE8F5E9) else Color.White,
        animationSpec = tween(300),
        label = "background"
    )

    val scale by animateFloatAsState(
        targetValue = if (dailyTaskModel.isCompleted) 0.98f else 1f,
        animationSpec = tween(200),
        label = "scale"
    )

    Card(
        modifier = modifier
            .clickable { onToggleComplete(dailyTaskModel.ticketId) },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(2.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (dailyTaskModel.isCompleted) 1.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = if (dailyTaskModel.isCompleted) Color(0xFF4CAF50) else Color.Transparent,
                        shape = CircleShape
                    )
                    .then(
                        if (!dailyTaskModel.isCompleted) Modifier.background(
                            color = Color.Transparent,
                            shape = CircleShape
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!dailyTaskModel.isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = Color.Transparent,
                                shape = CircleShape
                            )
                            .then(
                                Modifier.background(
                                    color = Color.Transparent,
                                    shape = CircleShape
                                )
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = Color.Transparent,
                                    shape = CircleShape
                                )
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                border = BorderStroke(2.dp, Color(0xFFBDBDBD))
                            ) {

                            }
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completada",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Color indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(
                        color = Color(0xFFBDBDBD),
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "test",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (dailyTaskModel.isCompleted) Color.Gray else Color.Black,
                    textDecoration = if (dailyTaskModel.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dailyTaskModel.time.format(formatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "TIPO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier
                            .background(
                                color = Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}