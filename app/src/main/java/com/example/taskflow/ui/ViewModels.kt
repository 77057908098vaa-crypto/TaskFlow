package com.example.taskflow.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.data.model.Priority
import com.example.taskflow.data.model.Project
import com.example.taskflow.data.model.Task
import com.example.taskflow.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    val projects = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun addProject(name: String, color: Long) = viewModelScope.launch {
        repository.addProject(Project(name = name, color = color))
    }
    fun deleteProject(project: Project) = viewModelScope.launch {
        repository.deleteProject(project)
    }
}

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    val allTasks = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun getTasksForProject(projectId: Long): StateFlow<List<Task>> =
        repository.getTasksByProject(projectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun addTask(task: Task) = viewModelScope.launch { repository.addTask(task) }
    fun updateTask(task: Task) = viewModelScope.launch { repository.updateTask(task) }
    fun updatePriority(task: Task, priority: Priority) = viewModelScope.launch {
        repository.updateTask(task.copy(priority = priority))
    }
    fun deleteTask(task: Task) = viewModelScope.launch { repository.deleteTask(task) }
}
