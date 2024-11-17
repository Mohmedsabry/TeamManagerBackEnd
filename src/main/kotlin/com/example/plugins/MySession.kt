package com.example.plugins

import kotlinx.serialization.Serializable

@Serializable
data class MySession(
    val sessionId: String,
    val sender: String,
    val receiver: String,
)
