package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val user: UserDto,
    val message: String,
    val time: Long,
)
