package com.example.data.mapper

import com.example.data.mongodb.entity.TaskEntity
import com.example.domain.model.Task

fun TaskEntity.toTask(): Task {
    return Task(
        id = this.id,
        creator = this.creator,
        description = this.description,
        due = due,
        status = this.status,
        deadLine = deadLine,
        createdTime = createdTime,
        teamId = this.teamId,
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        creator = this.creator,
        description = this.description,
        due = due,
        status = this.status,
        deadLine = deadLine,
        createdTime = createdTime,
        teamId = this.teamId,
    )
}