package com.example.data.mapper

import com.example.data.mongodb.entity.Members
import com.example.domain.model.Member

fun Members.toMember(): Member =
    Member(
        email = email,
        tasks = taskEntities.map { it.toTask() },
        teams = teams,
        name = name,
        image = image,
        gender = gender
    )

fun Member.toMemberEntity(): Members = Members(
    email = email,
    taskEntities = tasks.map { it.toEntity() },
    teams = teams,
    name = name,
    image = image,
    gender = gender
)
