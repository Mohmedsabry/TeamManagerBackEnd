package com.example.data.mongodb.entity

import org.bson.codecs.pojo.annotations.BsonId


data class Members(
    @BsonId
    val email: String,
    var taskEntities: List<TaskEntity> = listOf(),
    var teams: List<String> = listOf(),
    val name: String,
    val image: String,
    val gender: String
)
