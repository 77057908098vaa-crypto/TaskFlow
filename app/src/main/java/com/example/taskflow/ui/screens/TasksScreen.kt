package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskflow.data.model.Priority
import com.example.taskflow.data.model.Task
import com.example.taskflow.data.model.TaskStatus
import com.example.taskflow.ui.TaskViewModel

fun Priority.label() = when (this) {
    Priority.URGENT_IMPORTANT -> "🔴 Срочно и важно"
    Priority.IMPORTANT -> "🟡 Важно, не срочно"
    Priority.URGENT -> "🟠 Срочно, не важно"
    Priority.NEITHER -> "⚪ Не срочно / не важно"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    projectId: Long,
    projectName: String,
    onBack: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.getTasksForProject(projectId).collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.NEITHER) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(projectName) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.id }) { task ->
                TaskCard(task = task, onStatusChange = { viewModel.updateTask(task.copy(status = it)) }, onDelete = { viewModel.deleteTask(task) })
            }
        }
    }
    if (showDialog) {
        var expanded by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Новая задача") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newTaskTitle, onValueChange = { newTaskTitle = it }, label = { Text("Название") }, singleLine = true)
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(value = selectedPriority.label(), onValueChange = {}, readOnly = true, label = { Text("Приоритет") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor())
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            Priority.entries.forEach { p -> DropdownMenuItem(text = { Text(p.label()) }, onClick = { selectedPriority = p; expanded = false }) }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newTaskTitle.isNotBlank()) {
                        viewModel.addTask(Task(projectId = projectId, title = newTaskTitle, priority = selectedPriority))
                        newTaskTitle = ""; showDialog = false
                    }
                }) { Text("Добавить") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Отмена") } }
        )
    }
}

@Composable
fun TaskCard(task: Task, onStatusChange: (TaskStatus) -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.status == TaskStatus.DONE, onCheckedChange = { onStatusChange(if (it) TaskStatus.DONE else TaskStatus.TODO) })
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(task.title, style = MaterialTheme.typography.bodyLarge)
                Text(task.priority.label(), style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
        }
    }
}
