package com.example.data.mongodb.entity

import com.example.data.mongodb.util.TaskStatue
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

data class TaskEntity(
    @BsonId
    val id: String = ObjectId().toString(),
    val creator: String,
    val description: String,
    val due: String,
    val status: String = TaskStatue.IN_PROGRESS.toString().lowercase(Locale.getDefault()),
    val deadLine: Long,
    val createdTime: Long = System.currentTimeMillis(),
    var teamId: String
)
