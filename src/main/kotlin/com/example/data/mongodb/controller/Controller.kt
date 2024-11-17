package com.example.data.mongodb.controller

import com.example.data.mongodb.entity.*
import com.example.data.mongodb.util.RequestStatue
import com.example.data.mongodb.util.TaskStatue

interface Controller {
    suspend fun getMember(email: String): Members
    suspend fun addMember(member: Members)
    suspend fun addTask(taskEntity: TaskEntity)
    suspend fun deleteTask(taskEntity: TaskEntity)
    suspend fun addTeam(teamEntity: TeamEntity): Map<String, List<String>>
    suspend fun deleteMember(email: String, teamId: String)
    suspend fun deleteTeam(teamId: String)
    suspend fun getTeam(teamId: String): TeamEntity?
    suspend fun updateTaskStatue(
        statue: TaskStatue,
        taskFinisher: String
    )
    suspend fun addMemberToTeam(teamId: String, memberEmail: String)
    suspend fun getAllMembers(): List<Members>
    suspend fun joinTeam(teamId: String, memberEmail: String, admin: String)
    suspend fun getAllMembersForTeam(teamId: String): List<Members>
    suspend fun getAllTasksForTeam(teamId: String): List<TaskEntity>
    suspend fun getAllTasksForMember(member: String): List<TaskEntity>
    suspend fun getAllTeamsForMember(memberEmail: String): List<TeamEntity>
    suspend fun getAllRequestsForMember(memberEmail: String): List<RequestEntity>
    suspend fun updateRequestStatue(
        statue: RequestStatue,
        id: String
    )

    suspend fun getNotification(who: String): List<NotificationEntity>
    suspend fun clearNotification(who: String)
    suspend fun getChatMessages(
        sender: String,
        receiver: String,
    ): List<ChatEntity>

    suspend fun sendMessage(
        sender: String,
        receiver: String,
        message: String
    )

    suspend fun requestNewPassword(
        sender: String,
        resetCode: String
    )

    suspend fun removeRequestForPassword(
        sender: String
    )

    suspend fun getRequestTime(
        sender: String,
    ): ForgetPasswordEntity?
}