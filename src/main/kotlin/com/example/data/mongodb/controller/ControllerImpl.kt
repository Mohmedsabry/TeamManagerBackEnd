package com.example.data.mongodb.controller

import com.example.data.mongodb.entity.*
import com.example.data.mongodb.util.RequestStatue
import com.example.data.mongodb.util.TaskStatue
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.set
import kotlinx.coroutines.*
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.text.SimpleDateFormat
import java.util.*

class ControllerImpl(
    private val db: CoroutineDatabase
) : Controller {
    override suspend fun getMember(email: String): Members {
        return memberCollection.findOneById(email) ?: Members(
            email,
            name = "",
            image = "",
            gender = ""
        )
    }

    private val memberCollection = db.getCollection<Members>()
    private val taskEntityCollection = db.getCollection<TaskEntity>()
    private val teamEntityCollection = db.getCollection<TeamEntity>()
    private val chatCollection = db.getCollection<ChatEntity>()
    private val notificationCollection = db.getCollection<NotificationEntity>()
    private val requestCollection = db.getCollection<RequestEntity>()
    private val forgetPasswordCollection = db.getCollection<ForgetPasswordEntity>()
    override suspend fun addMember(member: Members) {
        memberCollection.insertOne(member)
    }

    override suspend fun addTask(taskEntity: TaskEntity) {
        taskEntityCollection.insertOne(taskEntity)
        val email = taskEntity.due
        val member = memberCollection.findOneById(email)
        member?.let {
            val updatedList = it.taskEntities + taskEntity
            memberCollection.updateOneById(
                email, it.copy(
                    taskEntities = updatedList
                )
            )
        }
        val team = teamEntityCollection.findOneById(taskEntity.teamId)
        team?.let {
            val updatedList = it.taskEntities + taskEntity
            teamEntityCollection.updateOneById(
                team.id, it.copy(
                    taskEntities = updatedList
                )
            )
        }
        val deadLine = taskEntity.deadLine
        val formatter = SimpleDateFormat("yyyy mm dd")
        val deadlineDate = formatter.format(Date(deadLine))
        notificationCollection.insertOne(
            NotificationEntity(
                who = email,
                message = "there is task assigned: ${taskEntity.description} from: ${taskEntity.creator} deadLine: $deadlineDate in team: ${team?.name}"
            )
        )
    }

    override suspend fun addTeam(teamEntity: TeamEntity): Map<String, List<String>> {
        println(teamEntity)
        return coroutineScope {
            teamEntity.taskEntities.map {
                async { it.teamId = teamEntity.id }
            }.awaitAll()
            teamEntity.members.map { member ->
                async {
                    member.taskEntities += teamEntity.taskEntities.filter { it.due == member.email }
                    member.teams += teamEntity.id
                }
            }.awaitAll()
            teamEntityCollection.insertOne(teamEntity)
            taskEntityCollection.insertMany(teamEntity.taskEntities)
            val members = teamEntity.members
            members.map { member ->
                async {
                    val dbMember = memberCollection.findOneById(member.email)
                    println(dbMember)
                    dbMember?.let {
                        memberCollection.updateOneById(
                            it.email,
                            it.copy(
                                teams = it.teams + teamEntity.id,
                                taskEntities = it.taskEntities + teamEntity.taskEntities.filter { taskEntity -> taskEntity.due == it.email || taskEntity.creator == it.email }
                            )
                        )
                        notificationCollection.insertOne(
                            NotificationEntity(
                                who = member.email,
                                message = "there is team you joined ${teamEntity.name} id: ${teamEntity.id}"
                            )
                        )
                    }
                }
            }.awaitAll()
            val adminMember = memberCollection.findOneById(teamEntity.admin)
            adminMember?.let { member ->
                memberCollection.updateOneById(
                    member.email,
                    member.copy(
                        teams = member.teams + teamEntity.id,
                        taskEntities = member.taskEntities + teamEntity.taskEntities.filter { member.email == it.due || member.email == it.creator }
                    )
                )
            }
            mapOf(
                "teamId" to listOf(teamEntity.id),
                "tasks" to teamEntity.taskEntities.map { it.id }
            )
        }
    }

    override suspend fun deleteMember(
        email: String,
        teamId: String
    ) {
        coroutineScope {
            val member = memberCollection.findOneById(email)
            val team = teamEntityCollection.findOneById(teamId)
            async {
                team?.let {
                    teamEntityCollection.updateOneById(
                        teamId,
                        it.copy(
                            members = it.members - member!!,
                            taskEntities = it.taskEntities - member.taskEntities.toSet()
                        )
                    )
                }
            }.await()
            async {
                taskEntityCollection.deleteMany(
                    `in`("id", member!!.taskEntities.map { it.id })
                )
            }.await()
        }
    }

    override suspend fun deleteTeam(teamId: String) {
        coroutineScope {
            val team = teamEntityCollection.findOneById(teamId)
            team?.let {
                val deleteTeam = async { teamEntityCollection.deleteOneById(team.id) }
                val updateMembers = it.members.map { member ->
                    async {
                        val updatedList = member.teams - teamId
                        val taskList = member.taskEntities - it.taskEntities.toSet()
                        memberCollection.updateOne(
                            eq("_id", member.email),
                            member.copy(teams = updatedList, taskEntities = taskList)
                        )
                    }
                }
                val deleteTasks = it.taskEntities.map { task ->
                    async {
                        taskEntityCollection.deleteOneById(task.id)
                    }
                }
                (deleteTasks + updateMembers + deleteTeam).awaitAll()
            }
        }
    }

    override suspend fun getTeam(teamId: String): TeamEntity? {
        return teamEntityCollection.findOneById(teamId)
    }

    override suspend fun updateTaskStatue(
        statue: TaskStatue,
        taskFinisher: String
    ) {
        taskEntityCollection.findOne(eq("due", taskFinisher))?.let { task ->
            taskEntityCollection.updateOne(
                eq("id", task.id),
                task.copy(
                    status = statue.name,
                )
            )
            memberCollection.findOne(eq("email", taskFinisher))?.let {
                memberCollection.updateOne(
                    eq("email", it.email),
                    it.copy(
                        taskEntities = it.taskEntities - task
                    )
                )
            }
        }
    }

    override suspend fun getAllMembers(): List<Members> {
        return memberCollection.find()
            .toList()
    }

    override suspend fun joinTeam(
        teamId: String,
        memberEmail: String,
        admin: String
    ) {
        requestCollection.insertOne(
            RequestEntity(
                admin = admin,
                due = memberEmail,
                teamId = teamId
            )
        )
    }

    override suspend fun getAllMembersForTeam(
        teamId: String
    ): List<Members> {
        return teamEntityCollection.findOneById(
            teamId
        )?.members ?: listOf()
    }

    override suspend fun deleteTask(taskEntity: TaskEntity) {
        withContext(Dispatchers.IO) {
            async { taskEntityCollection.deleteOneById(taskEntity.id) }.await()
            async {
                teamEntityCollection.findOneById(taskEntity.teamId)?.let { team ->
                    teamEntityCollection.updateOneById(
                        team.id,
                        team.copy(
                            taskEntities = team.taskEntities - taskEntity,
                            members = team.members.map { it.copy(taskEntities = it.taskEntities - taskEntity) },
                        )
                    )
                }
            }.await()
            async {
                memberCollection.findOneById(taskEntity.due)?.let { member ->
                    memberCollection.updateOneById(
                        member.email,
                        member.copy(taskEntities = member.taskEntities - taskEntity)
                    )
                }
            }.await()
        }
    }

    override suspend fun getAllTasksForTeam(
        teamId: String
    ): List<TaskEntity> {
        return teamEntityCollection.findOneById(
            teamId
        )?.taskEntities ?: listOf()
    }

    override suspend fun getAllTasksForMember(
        member: String
    ): List<TaskEntity> {
        return memberCollection.findOne(eq("_id", member))
            ?.taskEntities ?: listOf()
    }

    override suspend fun getAllTeamsForMember(
        memberEmail: String
    ): List<TeamEntity> {
        return coroutineScope {
            memberCollection.findOneById(memberEmail)
                ?.teams?.map {
                    async {
                        teamEntityCollection.findOneById(
                            it
                        ) ?: TeamEntity(
                            name = "",
                            description = "",
                            admin = ""
                        )
                    }
                }?.awaitAll() ?: listOf()
        }
    }

    override suspend fun getAllRequestsForMember(
        memberEmail: String
    ): List<RequestEntity> {
        return requestCollection.find(
            eq("due", memberEmail)
        ).toList().filter {
            it.statues == RequestStatue.PENDING.name.lowercase(Locale.getDefault())
        }
    }

    override suspend fun updateRequestStatue(
        statue: RequestStatue,
        id: String
    ) {
        coroutineScope {
            val request = requestCollection.findOne(eq("id", id))
            request?.let {
                when (statue) {
                    RequestStatue.PENDING -> {}
                    RequestStatue.ACCEPTED -> {
                        async { acceptRequest(it.teamId, it.due) }.await()
                        async {
                            requestCollection.deleteOne(
                                eq("id", id)
                            )
                        }.await()
                    }

                    RequestStatue.REJECTED -> {
                        requestCollection.deleteOne(eq("id", id))
                    }
                }
            }
        }
    }

    override suspend fun addMemberToTeam(teamId: String, memberEmail: String) {
        val team = teamEntityCollection.findOneById(teamId)
        val member = memberCollection.findOneById(memberEmail)
        team?.let { teamEntity ->
            member?.let { memberEntity ->
                teamEntityCollection.updateOneById(
                    teamEntity.id,
                    teamEntity.copy(
                        members = teamEntity.members + memberEntity,
                    )
                )
                memberCollection.updateOneById(
                    memberEntity.email,
                    memberEntity.copy(
                        teams = memberEntity.teams + teamEntity.id
                    )
                )
            }
        }
    }

    private suspend fun acceptRequest(teamId: String, memberEmail: String) {
        val team = teamEntityCollection.findOneById(teamId)
        val member = memberCollection.findOneById(memberEmail)
        team?.let { teamEntity ->
            member?.let { memberEntity ->
                teamEntityCollection.updateOneById(
                    teamEntity.id,
                    teamEntity.copy(
                        members = teamEntity.members + memberEntity,
                    )
                )
                memberCollection.updateOneById(
                    memberEntity.email,
                    memberEntity.copy(
                        teams = memberEntity.teams + teamEntity.id
                    )
                )
            }
        }
    }

    override suspend fun getNotification(
        who: String
    ): List<NotificationEntity> {
        return notificationCollection.find(eq("_id", who))
            .ascendingSort(NotificationEntity::time)
            .toList()
    }

    override suspend fun clearNotification(who: String) {
        notificationCollection.deleteMany(
            eq("_id", who)
        )
    }

    override suspend fun getChatMessages(
        sender: String,
        receiver: String
    ): List<ChatEntity> {
        val exp = and(
            or(
                eq("sender", sender),
                eq("sender", receiver)
            ),
            or(
                eq("receiver", sender),
                eq("receiver", receiver)
            )
        )
        return chatCollection.find(exp)
            .descendingSort(ChatEntity::time)
            .toList()
    }

    override suspend fun sendMessage(sender: String, receiver: String, message: String) {
        chatCollection.insertOne(
            ChatEntity(
                sender = sender,
                receiver = receiver,
                message = message
            )
        )
    }

    override suspend fun requestNewPassword(
        sender: String,
        resetCode: String
    ) {
        val expirationTime = System.currentTimeMillis() + (1000 * 15 * 60) // 15 minutes from now
        memberCollection.findOneById(sender) ?: throw Exception()
        val updateResult = forgetPasswordCollection.updateOne(
            eq("_id", sender),
            combine(
                set("restCode", resetCode),
                set("expirationTime", expirationTime)
            ),
            UpdateOptions().upsert(true) // Use upsert to insert if not exists
        )

        if (updateResult.matchedCount.toInt() == 0 && updateResult.upsertedId != null) {
            println("New reset password entry created for $sender")
        } else {
            println("Reset password entry updated for $sender")
        }

    }

    override suspend fun removeRequestForPassword(sender: String) {
        db.getCollection<ForgetPasswordEntity>()
            .deleteOne(eq("_id", sender))
    }

    override suspend fun getRequestTime(sender: String):
            ForgetPasswordEntity? {
        return db.getCollection<ForgetPasswordEntity>()
            .findOne(eq("_id", sender))
    }
}