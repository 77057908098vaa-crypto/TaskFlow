package com.example.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.taskflow.ui.screens.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TaskFlowApp() }
    }
}

@Composable
fun TaskFlowApp() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    MaterialTheme {
        Scaffold(
            bottomBar = {
                if (currentRoute in listOf("projects", "matrix", "kanban")) {
                    NavigationBar {
                        NavigationBarItem(selected = currentRoute == "projects", onClick = { navController.navigate("projects") { launchSingleTop = true } }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Проекты") })
                        NavigationBarItem(selected = currentRoute == "matrix", onClick = { navController.navigate("matrix") { launchSingleTop = true } }, icon = { Icon(Icons.Default.DateRange, null) }, label = { Text("Матрица") })
                        NavigationBarItem(selected = currentRoute == "kanban", onClick = { navController.navigate("kanban") { launchSingleTop = true } }, icon = { Icon(Icons.Default.Dashboard, null) }, label = { Text("Kanban") })
                    }
                }
            }
        ) { _ ->
            NavHost(navController = navController, startDestination = "projects") {
                composable("projects") {
                    ProjectsScreen(onProjectClick = { id, name -> navController.navigate("tasks/$id/$name") })
                }
                composable(
                    "tasks/{projectId}/{projectName}",
                    arguments = listOf(navArgument("projectId") { type = NavType.LongType }, navArgument("projectName") { type = NavType.StringType })
                ) { backStack ->
                    TasksScreen(
                        projectId = backStack.arguments!!.getLong("projectId"),
                        projectName = backStack.arguments!!.getString("projectName") ?: "",
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("matrix") { EisenhowerScreen() }
                composable("kanban") { KanbanScreen() }
            }
        }
    }
}
