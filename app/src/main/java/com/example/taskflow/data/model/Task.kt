package com.example.taskflow.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority { URGENT_IMPORTANT, IMPORTANT, URGENT, NEITHER }
enum class TaskStatus { TODO, IN_PROGRESS, DONE }

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.NEITHER,
    val status: TaskStatus = TaskStatus.TODO,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
