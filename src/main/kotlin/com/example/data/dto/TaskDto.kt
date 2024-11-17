package com.example.data.dto

import com.example.data.mongodb.util.TaskStatue
import com.example.domain.model.Task
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String = "",
    val creator: String,
    val description: String,
    val due: String,
    val status: String = TaskStatue.IN_PROGRESS.toString().lowercase(),
    val deadLine: Long,
    val createdTime: Long = System.currentTimeMillis(),
    val teamId: String
) {
    fun toTask(): Task = Task(
        id = id,
        creator = creator,
        description = description,
        due = due,
        status = status,
        deadLine = deadLine,
        createdTime = createdTime,
        teamId = teamId
    )
}