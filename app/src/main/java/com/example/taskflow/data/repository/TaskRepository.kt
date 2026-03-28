package com.example.taskflow.data.repository

import com.example.taskflow.data.db.ProjectDao
import com.example.taskflow.data.db.TaskDao
import com.example.taskflow.data.model.Project
import com.example.taskflow.data.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao
) {
    fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()
    fun getTasksByProject(projectId: Long): Flow<List<Task>> = taskDao.getTasksByProject(projectId)
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    suspend fun addProject(project: Project) = projectDao.insertProject(project)
    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project)
    suspend fun addTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}
