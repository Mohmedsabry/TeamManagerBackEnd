package com.example.data.dto

import com.example.domain.model.Team
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val name: String,
    val description: String,
    val admin: String,
    val members: List<MemberDto> = listOf(),
    val taskEntities: List<TaskDto> = listOf(),
    val timeCreated: Long = System.currentTimeMillis(),
    val teamId: String
) {
    fun toTeam(): Team = Team(
        name = name,
        description = description,
        admin = admin,
        members = members.map { it.toMember() },
        taskEntities = taskEntities.map { it.toTask() },
        timeCreated = timeCreated
    )
}
