package com.example.dailytasks.addtasks

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytasks.R

@Composable
fun ScreenHeader(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .clickable(onClick = onNavigateUp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = "←",
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            )
        }
        Column {
            Text(
                text  = stringResource(R.string.new_task_header),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    color         = MaterialTheme.colorScheme.onSurface,
                ),
            )
            Text(
                text  = stringResource(R.string.new_task_subheader),
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            )
        }
    }
}