package com.example.domain.model

import com.example.data.dto.NotificationDto

data class Notification(
    val who: String,
    val message: String,
    val time: Long = System.currentTimeMillis(),
){
    fun toDto(): NotificationDto {
        return NotificationDto(
            who, message, time
        )
    }
}
