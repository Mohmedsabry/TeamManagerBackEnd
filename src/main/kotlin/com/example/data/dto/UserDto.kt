package com.example.data.dto

import com.example.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val username: String,
    val password: String,
    val email: String,
    val age: Double,
    val gender: String,
    val phoneNumber: String,
    val image: String
) {
    fun toUser(): User = User(
        username = username,
        password = password,
        email = email,
        age = age,
        gender = gender,
        phoneNumber = phoneNumber,
        image = image
    )
}
