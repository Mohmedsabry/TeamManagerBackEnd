package com.example.domain.model

import com.example.data.dto.MessageDto
import com.example.data.mapper.toDto

data class Chat(
    val user: User,
    val time: Long = System.currentTimeMillis(),
    val message: String
) {
    fun toDto(): MessageDto = MessageDto(
        user = user.toDto(),
        time = time,
        message = message
    )
}
