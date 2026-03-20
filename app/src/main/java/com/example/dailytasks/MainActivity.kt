package com.example.dailytasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dailytasks.addtasks.AddTaskScreen
import com.example.dailytasks.core.ui.navigation.MainNavigation
import com.example.dailytasks.core.ui.theme.DailyTasksTheme
import com.example.dailytasks.home.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyTasksTheme {
                val navController = rememberNavController()

                NavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    startDestination = MainNavigation.Home
                ){
                    composable<MainNavigation.Home> {
                        MainScreen(
                            onNavigateToAddTask ={ ticketId ->
                                navController.navigate(MainNavigation.AddTask(ticketId))
                            }
                        )
                    }
                    composable<MainNavigation.AddTask> {
                        AddTaskScreen(
                            onNavigateToHomeScreen = {
                                navController.navigate(MainNavigation.Home)
                            }
                        )
                    }
                }
            }
        }
    }
}

