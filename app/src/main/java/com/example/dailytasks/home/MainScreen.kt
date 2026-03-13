package com.example.dailytasks.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailytasks.R
import com.example.dailytasks.core.domain.TaskStatus
import com.example.dailytasks.core.ui.TicketListComposable
import com.example.dailytasks.core.ui.composables.HeaderSection

@Composable
fun MainScreen(
    onNavigateToAddTask : () -> Unit = {},
    viewModel : TaskViewModel = hiltViewModel()
){
    val today by viewModel.selectedDay.collectAsState()
    val dailyTaskModels by viewModel.dayTickets.collectAsState()



    Scaffold(modifier = Modifier
        .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_task_description),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        topBar = {
            HeaderSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(24.dp),
                date = today,
                onDateSelected = {
                    viewModel.changeDay(it)
                }
            )
        }
    ){ paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            TicketListComposable(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                dateFilter = today,
                taskList = dailyTaskModels,
                onToggleComplete = { ticketId ->
                    val status = dailyTaskModels.find { it.ticketId == ticketId }?.status
                    val newStatus = if (status == TaskStatus.PENDING) TaskStatus.COMPLETED else TaskStatus.PENDING
                    viewModel.updateCompletionTask(ticketId, newStatus)
                }

            )
        }
    }
}
