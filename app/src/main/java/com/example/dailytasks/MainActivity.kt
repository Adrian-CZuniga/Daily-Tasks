package com.example.dailytasks

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.dailytasks.model.DayTicketModel
import com.example.dailytasks.model.generateTicketsModel
import com.example.dailytasks.ui.composables.HeaderSection
import com.example.dailytasks.ui.composables.TicketListItem
import com.example.dailytasks.ui.theme.DailyTasksTheme
import com.example.dailytasks.viewModel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Clock
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyTasksTheme {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            Log.d("MainActivity", "FloatingActionButton")
                        },
                            containerColor = Color.DarkGray) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                        }
                    }
                ) {
                    MainScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel : TaskViewModel = hiltViewModel()){
    val today = LocalDate.now(Clock.systemDefaultZone())
    val taskSequenceLimitModel = viewModel.getTaskSequenceLimitModel()

    val paginationTickets = taskSequenceLimitModel.generateTicketsModel(fromDate = today, toDate = today.plusDays(21))

    val date = today

    Scaffold(modifier = modifier
        .fillMaxSize(),
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

@Composable
fun TicketListComposable(modifier: Modifier = Modifier, dateFilter : LocalDate? = null, taskList : List<DayTicketModel>){
    LazyColumn (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(taskList.filter { dateFilter == null || it.date == dateFilter }.size){
            TicketListItem(
                modifier = Modifier.fillMaxWidth(),
                dayTicketModel = taskList[it],
                onToggleComplete = {

                }
            )
        }
    }
}