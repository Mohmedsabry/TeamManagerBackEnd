package com.example.data.chat

import io.ktor.websocket.*

data class ChatMember(
    val sender: String,
    val receiver: String,
    val sessionId: String,
    val socket: WebSocketSession
)
