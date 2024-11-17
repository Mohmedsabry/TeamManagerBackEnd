package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RequestPasswordDto(
    val email: String,
    val restCode: String,
)
