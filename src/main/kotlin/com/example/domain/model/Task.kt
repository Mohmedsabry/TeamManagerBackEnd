package com.example.domain.model

import com.example.data.dto.TaskDto
import com.example.data.mongodb.util.TaskStatue

data class Task(
    val id: String,
    val creator: String,
    val description: String,
    val due: String,
    val status: String = TaskStatue.IN_PROGRESS.toString().lowercase(),
    val deadLine: Long,
    val createdTime: Long = System.currentTimeMillis(),
    val teamId: String
) {
    fun toDto(): TaskDto {
        return TaskDto(
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
}
