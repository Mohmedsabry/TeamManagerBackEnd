package com.example.data.mapper

import com.example.data.mongodb.entity.RequestEntity
import com.example.domain.model.Request

fun RequestEntity.toRequest(): Request {
    return Request(
        id = this.id,
        admin = admin,
        due = due,
        time = time,
        teamId = this.teamId,
        statues = statues
    )
}