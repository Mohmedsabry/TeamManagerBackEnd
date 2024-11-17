package com.example.data.mapper

import com.example.data.mongodb.entity.TeamEntity
import com.example.domain.model.Team

fun Team.toEntity(): TeamEntity =
    TeamEntity(
        name = this.name,
        description = this.description,
        admin = admin,
        members = this.members.map { it.toMemberEntity() },
        taskEntities = taskEntities.map { it.toEntity() },
        timeCreated = timeCreated,
    )

fun TeamEntity.toTeam() = Team(
    id = this.id,
    name = this.name,
    description = this.description,
    admin = admin,
    members = this.members.map { members ->
        members.toMember()
    },
    taskEntities = this.taskEntities.map { it.toTask() },
    timeCreated = timeCreated,
)