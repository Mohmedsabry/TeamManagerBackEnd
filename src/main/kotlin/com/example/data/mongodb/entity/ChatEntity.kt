package com.example.data.mongodb.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class ChatEntity(
    @BsonId
    val id: String = ObjectId().toString(),
    val sender: String,
    val receiver: String,
    val time: Long = System.currentTimeMillis(),
    val message: String
)
