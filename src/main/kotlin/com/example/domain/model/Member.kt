package com.example.domain.model

import com.example.data.dto.MemberDto

data class Member(
    val email: String,
    val tasks: List<Task> = listOf(),
    val teams: List<String> = listOf(),
    val name: String,
    val image: String,
    val gender: String
) {
    fun toDto(): MemberDto {
        return MemberDto(
            email,
            tasks = tasks.map { it.toDto() },
            teams = teams,
            name = name,
            image = image,
            gender = gender
        )
    }
}
