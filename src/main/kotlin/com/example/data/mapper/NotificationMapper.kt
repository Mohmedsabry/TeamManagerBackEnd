package com.example.data.mapper

import com.example.data.mongodb.entity.NotificationEntity
import com.example.domain.model.Notification

fun NotificationEntity.toNotification(): Notification {
    return Notification(
        who = who,
        message = message,
        time = time
    )
}