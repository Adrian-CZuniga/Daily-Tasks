package com.example.dailytasks

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailytasks.model.DayTicketModel
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
                Scaffold(){
                    MainScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel : TaskViewModel = hiltViewModel()){
    val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now(Clock.systemDefaultZone())
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    Column ( modifier = modifier.fillMaxSize() ){
        TicketListComposable(taskList = viewModel.getTicketsByDate(date))
    }
}

@Composable
fun TicketListComposable(taskList : List<DayTicketModel>, modifier: Modifier = Modifier){
    LazyColumn (modifier = modifier) {
        items(taskList.size){
            TicketListItem(dayTicketModel = taskList[it],
                modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun TicketListItem(dayTicketModel: DayTicketModel, modifier: Modifier = Modifier){
    Row (modifier = modifier) {
        Text(text = dayTicketModel.name)
        Text(text = dayTicketModel.date.toString())
    }
}

