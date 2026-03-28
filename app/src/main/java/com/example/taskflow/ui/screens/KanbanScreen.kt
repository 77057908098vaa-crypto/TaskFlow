package com.example.taskflow.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.taskflow.data.model.Task
import com.example.taskflow.data.model.TaskStatus
import com.example.taskflow.ui.TaskViewModel

data class KanbanColumn(val status: TaskStatus, val title: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanScreen(viewModel: TaskViewModel = hiltViewModel()) {
    val allTasks by viewModel.allTasks.collectAsState()
    var draggedTaskId by remember { mutableStateOf<Long?>(null) }
    val columns = listOf(
        KanbanColumn(TaskStatus.TODO, "📋 To Do", Color(0xFFE3F2FD)),
        KanbanColumn(TaskStatus.IN_PROGRESS, "⚡ In Progress", Color(0xFFFFF8E1)),
        KanbanColumn(TaskStatus.DONE, "✅ Done", Color(0xFFE8F5E9))
    )
    Scaffold(topBar = { TopAppBar(title = { Text("Kanban") }) }) { padding ->
        Row(modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            columns.forEach { column ->
                KanbanColumnView(
                    modifier = Modifier.weight(1f), column = column,
                    tasks = allTasks.filter { it.status == column.status },
                    onDragStart = { taskId -> draggedTaskId = taskId },
                    onDrop = { targetStatus ->
                        draggedTaskId?.let { id -> allTasks.find { it.id == id }?.let { viewModel.updateTask(it.copy(status = targetStatus)) } }
                        draggedTaskId = null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KanbanColumnView(modifier: Modifier, column: KanbanColumn, tasks: List<Task>, onDragStart: (Long) -> Unit, onDrop: (TaskStatus) -> Unit) {
    var isHovered by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(if (isHovered) column.color.copy(alpha = 0.5f) else column.color, label = "hover")
    val dropTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean { onDrop(column.status); isHovered = false; return true }
            override fun onEntered(event: DragAndDropEvent) { isHovered = true }
            override fun onExited(event: DragAndDropEvent) { isHovered = false }
        }
    }
    Card(modifier = modifier.fillMaxHeight().dragAndDropTarget(shouldStartDragAndDrop = { true }, target = dropTarget), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row { Text(column.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge); Spacer(Modifier.width(4.dp)); Badge { Text("${tasks.size}") } }
            Spacer(Modifier.height(8.dp)); HorizontalDivider(); Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(tasks, key = { it.id }) { task -> DraggableTaskCard(task = task, onDragStart = onDragStart) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableTaskCard(task: Task, onDragStart: (Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().dragAndDropSource {
            detectTapGestures(onLongPress = {
                onDragStart(task.id)
                startTransfer(DragAndDropTransferData(clipData = android.content.ClipData.newPlainText("taskId", task.id.toString())))
            })
        },
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(task.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            SuggestionChip(onClick = {}, label = { Text(task.priority.label(), style = MaterialTheme.typography.labelSmall) })
        }
    }
}
