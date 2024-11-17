package com.example.data.dto

import com.example.data.mongodb.util.RequestStatue
import kotlinx.serialization.Serializable
import java.util.*
@Serializable
data class RequestDto(
    val id: String,
    val admin: String,
    val time: Long = System.currentTimeMillis(),
    val teamId: String,
    val statues: String = RequestStatue.PENDING.name.lowercase(Locale.getDefault()),
)
