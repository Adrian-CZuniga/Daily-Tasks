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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailytasks.core.domain.generateTicketsModel
import com.example.dailytasks.core.ui.TicketListComposable
import com.example.dailytasks.core.ui.composables.HeaderSection
import java.time.Clock
import java.time.LocalDate

@Composable
fun MainScreen(
    onNavigateToAddTask : () -> Unit = {},
    viewModel : TaskViewModel = hiltViewModel()
){
    val today = LocalDate.now(Clock.systemDefaultZone())
    val taskSequenceLimitModel = viewModel.getTaskSequenceLimitModel()

    val paginationTickets = taskSequenceLimitModel.generateTicketsModel(fromDate = today, toDate = today.plusDays(21))

    val date = today

    Scaffold(modifier = Modifier
        .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = Color(0xFF3FA1C2)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White
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
                                Color(0xFF75BDDA),
                                Color(0xFF3FA1C2)
                            )
                        )
                    )
                    .padding(24.dp),
                date = date,
            )
        }
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)
        ) {
            TicketListComposable(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                dateFilter = date,
                taskList = paginationTickets
            )
        }
    }
}

