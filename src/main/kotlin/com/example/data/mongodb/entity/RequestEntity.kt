package com.example.data.mongodb.entity

import com.example.data.mongodb.util.RequestStatue
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

data class RequestEntity(
    @BsonId
    val id: String = ObjectId.get().toString(),
    val admin: String,
    val due: String,
    val time: Long = System.currentTimeMillis(),
    val teamId: String,
    val statues: String = RequestStatue.PENDING.name.lowercase(Locale.getDefault()),
)
