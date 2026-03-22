package com.example.dailytasks.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.dailytasks.core.ui.composables.DefaultError
import com.example.dailytasks.core.ui.composables.DefaultLoading
import com.example.dailytasks.core.ui.composables.HeaderSection
import com.example.dailytasks.core.ui.composables.StatusWrapper

@Composable
fun MainScreen(
    onNavigateToAddTask : (ticketId : String?) -> Unit = {},
    viewModel : TaskViewModel = hiltViewModel()
){
    val status by viewModel.status.collectAsState()
    val today by viewModel.selectedDay.collectAsState()
    val dailyTaskModels by viewModel.dayTickets.collectAsState()



    Scaffold(modifier = Modifier
        .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddTask(null) },
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
            StatusWrapper(
                status = status,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                loadingContent = {
                    DefaultLoading()
                },
                errorContent = {
                    DefaultError()
                }
            ) {
                TicketListComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    dateFilter = today,
                    taskList = dailyTaskModels,
                    onEdit = { ticketId ->
                        onNavigateToAddTask(ticketId)
                    },
                    onCancel = { ticketId ->
                        val ticket = dailyTaskModels.find { it.ticketId == ticketId }
                        val newStatus = if (ticket?.status == TaskStatus.CANCELLED) TaskStatus.PENDING else TaskStatus.CANCELLED
                        val newTicket = ticket?.copy(status = newStatus) ?: return@TicketListComposable
                        viewModel.updateCompletionTask(newTicket)
                    },
                    onToggleComplete = { ticketId ->
                        val ticket = dailyTaskModels.find { it.ticketId == ticketId }
                        if (ticket?.status == TaskStatus.CANCELLED) return@TicketListComposable

                        val newStatus = if (ticket?.status == TaskStatus.PENDING) TaskStatus.COMPLETED else TaskStatus.PENDING
                        val newTicket = ticket?.copy(status = newStatus) ?: return@TicketListComposable
                        viewModel.updateCompletionTask(newTicket)
                    }
                )
            }
        }
    }
}
