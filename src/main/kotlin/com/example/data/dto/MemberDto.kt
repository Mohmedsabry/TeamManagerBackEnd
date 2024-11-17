package com.example.data.dto

import com.example.domain.model.Member
import kotlinx.serialization.Serializable

@Serializable
data class MemberDto(
    val email: String,
    val tasks: List<TaskDto>,
    val teams: List<String>,
    val name: String,
    val image: String,
    val gender: String
) {
    fun toMember(): Member {
        return Member(
            email = email,
            name = name,
            image = image,
            tasks = tasks.map { it.toTask() },
            teams = teams,
            gender = gender
        )
    }
}
