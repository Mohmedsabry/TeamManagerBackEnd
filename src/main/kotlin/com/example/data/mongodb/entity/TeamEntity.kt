package com.example.data.mongodb.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class TeamEntity(
    @BsonId
    val id: String = ObjectId().toString(),
    val name: String,
    val description: String,
    val admin: String,
    val members: List<Members> = listOf(),
    val taskEntities: List<TaskEntity> = listOf(),
    val timeCreated: Long = System.currentTimeMillis(),
)
