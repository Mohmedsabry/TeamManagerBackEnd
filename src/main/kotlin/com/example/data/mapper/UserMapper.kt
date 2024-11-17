package com.example.data.mapper

import com.example.data.dto.UserDto
import com.example.domain.model.User

fun User.toDto(): UserDto = UserDto(
    username = this.username,
    email = this.email,
    password = this.password,
    age = age,
    gender = this.gender,
    phoneNumber = phoneNumber,
    image = image
)