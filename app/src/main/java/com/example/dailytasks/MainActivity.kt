package com.example.dailytasks

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailytasks.model.DayTicketModel
import com.example.dailytasks.model.generateTicketsModel
import com.example.dailytasks.ui.theme.DailyTasksTheme
import com.example.dailytasks.viewModel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Clock
import java.time.DayOfWeek
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
    var tabIndex by remember { mutableIntStateOf(today.dayOfWeek.value - 1) }

    val paginationTickets = taskSequenceLimitModel.generateTicketsModel(fromDate = today, toDate = today.plusDays(21))


    val date = today.plusDays(tabIndex.toLong())

    Column (modifier = modifier
        .fillMaxSize()
    ){
        TabRow(selectedTabIndex = tabIndex) {
            DayOfWeek.entries.forEachIndexed { index, title ->
                Tab(text = { Text(title.name) },
                    selected = tabIndex == title.value,
                    onClick = { tabIndex = index }
                )
            }
        }
        Text(text = "Today is ${date.dayOfWeek}")
        TicketListComposable(
            dateFilter = date,
            taskList = paginationTickets
        )
    }
}

@Composable
fun TicketListComposable(modifier: Modifier = Modifier, dateFilter : LocalDate? = null, taskList : List<DayTicketModel>){
    LazyColumn (modifier = modifier) {
        items(taskList.filter { dateFilter == null || it.date == dateFilter }.size){
            TicketListItem(dayTicketModel = taskList[it], modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun TicketListItem(dayTicketModel: DayTicketModel, modifier: Modifier = Modifier){
    Row (modifier = modifier) {
        Text(text = dayTicketModel.name)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = dayTicketModel.date.toString())
    }
}