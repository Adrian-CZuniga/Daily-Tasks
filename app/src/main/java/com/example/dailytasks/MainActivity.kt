package com.example.dailytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dailytasks.ui.theme.DailyTasksTheme
import com.example.dailytasks.viewModel.TaskViewModel

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
    Column ( modifier = modifier.fillMaxSize() ){
        Text(text = "Hola")

    }
}


