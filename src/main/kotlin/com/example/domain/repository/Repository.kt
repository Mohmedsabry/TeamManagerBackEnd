package com.example.domain.repository

import com.example.data.mongodb.util.RequestStatue
import com.example.data.mongodb.util.TaskStatue
import com.example.domain.model.*
import com.example.domain.util.GlobalError
import com.example.util.Result

interface Repository {
    suspend fun getMember(email: String): Result<GlobalError, Member>
    suspend fun addMember(member: Member): Result<GlobalError, Unit>
    suspend fun addTask(task: Task): Result<GlobalError, Unit>
    suspend fun deleteTask(task: Task): Result<GlobalError, Unit>
    suspend fun addTeam(team: Team): Result<GlobalError, Map<String, List<String>>>
    suspend fun deleteTeam(teamId: String): Result<GlobalError, Unit>
    suspend fun updateTaskStatue(
        statue: TaskStatue,
        taskFinisher: String
    ): Result<GlobalError, Unit>

    suspend fun getAllMember(): Result<GlobalError, List<Member>>
    suspend fun addMemberToTeam(memberEmail: String,teamId: String):Result<GlobalError, Unit>
    suspend fun getAllMemberForTeam(teamId: String): Result<GlobalError, List<Member>>
    suspend fun getAllTeamsForMember(memberId: String): Result<GlobalError, List<Team>>
    suspend fun getAllTasksForMember(email: String): Result<GlobalError, List<Task>>
    suspend fun getAllTasksForTeam(teamId: String): Result<GlobalError, List<Task>>
    suspend fun addMemberToTeam(
        teamId: String,
        memberEmail: String,
        admin: String
    ): Result<GlobalError, Unit>

    suspend fun deleteMemberFromTeam(
        teamId: String,
        memberEmail: String
    ): Result<GlobalError, Unit>

    suspend fun getAllRequestsForMember(
        memberEmail: String
    ): Result<GlobalError, List<Request>>

    suspend fun updateRequestStatue(
        statue: RequestStatue,
        id: String
    ): Result<GlobalError, Unit>

    suspend fun getNotification(
        who: String
    ): Result<GlobalError, List<Notification>>

    suspend fun clearNotification(who: String): Result<GlobalError, Unit>
    suspend fun getChatMessages(
        sender: String,
        receiver: String,
    ): Result<GlobalError, List<Chat>>

    suspend fun getTeam(
        teamId: String
    ): Result<GlobalError, Team>
}