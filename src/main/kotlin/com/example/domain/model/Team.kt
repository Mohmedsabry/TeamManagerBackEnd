package com.example.domain.model

import com.example.data.dto.TeamDto

data class Team(
    val id: String = "",
    val name: String,
    val description: String,
    val admin: String,
    val members: List<Member> = listOf(),
    val taskEntities: List<Task> = listOf(),
    val timeCreated: Long = System.currentTimeMillis(),
) {
    fun toDto(): TeamDto = TeamDto(
        name = name,
        description = description,
        admin = admin,
        members = members.map { it.toDto() },
        taskEntities = taskEntities.map { it.toDto() },
        timeCreated = timeCreated,
        teamId = id
    )
}
