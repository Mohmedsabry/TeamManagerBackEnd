package com.example.data.mongodb.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class NotificationEntity(
    @BsonId
    val id: String = ObjectId().toString(),
    val who: String,
    val message: String,
    val time: Long = System.currentTimeMillis(),
)
