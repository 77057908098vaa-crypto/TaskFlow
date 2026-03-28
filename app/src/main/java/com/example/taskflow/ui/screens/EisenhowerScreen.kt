package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskflow.data.model.Priority
import com.example.taskflow.data.model.Task
import com.example.taskflow.ui.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EisenhowerScreen(viewModel: TaskViewModel = hiltViewModel()) {
    val tasks by viewModel.allTasks.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Матрица Эйзенхауэра") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text("← Не срочно", style = MaterialTheme.typography.labelMedium)
                Text("Срочно →", style = MaterialTheme.typography.labelMedium)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuadrantBlock(Modifier.weight(1f), "🔴 Сделать сейчас", Color(0xFFFFCDD2), tasks.filter { it.priority == Priority.URGENT_IMPORTANT })
                QuadrantBlock(Modifier.weight(1f), "🟡 Запланировать", Color(0xFFFFF9C4), tasks.filter { it.priority == Priority.IMPORTANT })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuadrantBlock(Modifier.weight(1f), "🟠 Делегировать", Color(0xFFFFE0B2), tasks.filter { it.priority == Priority.URGENT })
                QuadrantBlock(Modifier.weight(1f), "⚪ Исключить", Color(0xFFE0E0E0), tasks.filter { it.priority == Priority.NEITHER })
            }
        }
    }
}

@Composable
fun QuadrantBlock(modifier: Modifier, title: String, color: Color, tasks: List<Task>) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = color)) {
        Column(Modifier.padding(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            if (tasks.isEmpty()) {
                Text("Нет задач", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            } else {
                tasks.take(8).forEach { task -> Text("• ${task.title}", style = MaterialTheme.typography.bodySmall) }
                if (tasks.size > 8) Text("+ ещё ${tasks.size - 8}...", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
