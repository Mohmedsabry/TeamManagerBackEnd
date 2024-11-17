package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val who: String,
    val message: String,
    val time: Long = System.currentTimeMillis(),
)