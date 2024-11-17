package com.example.data.mapper

import com.example.data.mongodb.entity.ChatEntity
import com.example.domain.model.Chat
import com.example.domain.model.User

fun ChatEntity.toChat(user: User) = Chat(
    message = message,
    time = time,
    user = user
)