package com.example.domain.model

import com.example.data.dto.RequestDto
import com.example.data.mongodb.util.RequestStatue
import java.util.*

data class Request(
    val id: String,
    val admin: String,
    val due: String,
    val time: Long = System.currentTimeMillis(),
    val teamId: String,
    val statues: String = RequestStatue.PENDING.name.lowercase(Locale.getDefault()),
) {
    fun toDto(): RequestDto {
        return RequestDto(
            admin = admin,
            teamId = teamId,
            time = time,
            statues = statues,
            id = id
        )
    }
}
