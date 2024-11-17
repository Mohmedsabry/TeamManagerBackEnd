package com.example.data.mongodb.entity

import org.bson.codecs.pojo.annotations.BsonId

data class ForgetPasswordEntity(
    @BsonId
    val email: String,
    val restCode: String?,
    val expirationTime: Long?
)
