package com.example.data.repositories

import com.example.data.db.UserDao
import com.example.data.mapper.*
import com.example.data.mongodb.controller.Controller
import com.example.data.mongodb.util.RequestStatue
import com.example.data.mongodb.util.TaskStatue
import com.example.domain.model.*
import com.example.domain.repository.Repository
import com.example.domain.util.GlobalError
import com.example.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepositoryImpl(
    private val controller: Controller,
    private val userDao: UserDao
) : Repository {
    override suspend fun getMember(
        email: String
    ): Result<GlobalError, Member> {
        return withContext(Dispatchers.IO) {
            try {
                val member = controller.getMember(email)
                Result.Success(member.toMember())
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.ThereIsNoUserFound)
            }
        }
    }

    override suspend fun addMember(
        member: Member
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.addMember(member.toMemberEntity())
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun addTask(
        task: Task
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.addTask(task.toEntity())
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun addTeam(
        team: Team
    ): Result<GlobalError, Map<String, List<String>>> {
        return withContext(Dispatchers.IO) {
            try {
                val data = controller.addTeam(team.toEntity())
                Result.Success(data)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun deleteTeam(
        teamId: String
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.deleteTeam(teamId)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun updateTaskStatue(
        statue: TaskStatue,
        taskFinisher: String
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.updateTaskStatue(
                    statue,
                    taskFinisher,
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun deleteTask(task: Task): Result<GlobalError, Unit> {
        return try {
            controller.deleteTask(task.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Failure(GlobalError.UnknownError)
        }
    }

    override suspend fun getAllMember(): Result<GlobalError, List<Member>> {
        return withContext(Dispatchers.IO) {
            try {
                val members = controller.getAllMembers().map {
                    it.toMember()
                }
                Result.Success(members)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun getAllMemberForTeam(
        teamId: String
    ): Result<GlobalError, List<Member>> {
        return withContext(Dispatchers.IO) {
            try {
                val members = controller.getAllMembersForTeam(teamId)
                    .map {
                        it.toMember()
                    }
                Result.Success(members)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun getAllTeamsForMember(
        memberId: String
    ): Result<GlobalError, List<Team>> {
        return withContext(Dispatchers.IO) {
            try {
                val teams = controller.getAllTeamsForMember(
                    memberEmail = memberId
                )
                Result.Success(teams.map {
                    it.toTeam()
                })
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun getAllTasksForMember(
        email: String
    ): Result<GlobalError, List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val tasks = controller.getAllTasksForMember(
                    member = email
                )
                Result.Success(tasks.map { it.toTask() })
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun getAllTasksForTeam(
        teamId: String
    ): Result<GlobalError, List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val tasks = controller.getAllTasksForTeam(
                    teamId = teamId
                )
                Result.Success(tasks.map { it.toTask() })
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun getTeam(
        teamId: String
    ): Result<GlobalError, Team> {
        return withContext(Dispatchers.IO) {
            try {
                val team =
                    controller.getTeam(teamId) ?: return@withContext Result.Failure(GlobalError.ThereIsNoUserFound)
                Result.Success(team.toTeam())
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun addMemberToTeam(
        teamId: String,
        memberEmail: String,
        admin: String
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.joinTeam(teamId, memberEmail, admin)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun deleteMemberFromTeam(
        teamId: String,
        memberEmail: String
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.deleteMember(memberEmail, teamId)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun getAllRequestsForMember(
        memberEmail: String
    ): Result<GlobalError, List<Request>> {
        return withContext(Dispatchers.IO) {
            try {
                val tasks = controller.getAllRequestsForMember(
                    memberEmail
                )
                Result.Success(tasks.map { it.toRequest() })
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun updateRequestStatue(
        statue: RequestStatue,
        id: String
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.updateRequestStatue(
                    statue = statue,
                    id = id
                )
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun addMemberToTeam(memberEmail: String, teamId: String): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.addMemberToTeam(teamId = teamId, memberEmail = memberEmail)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun getNotification(
        who: String
    ): Result<GlobalError, List<Notification>> {
        return withContext(Dispatchers.IO) {
            try {
                val notification = controller.getNotification(
                    who = who
                )
                Result.Success(notification.map { it.toNotification() })
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun clearNotification(
        who: String
    ): Result<GlobalError, Unit> {
        return withContext(Dispatchers.IO) {
            try {
                controller.clearNotification(who)
                Result.Success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }

    override suspend fun getChatMessages(
        sender: String,
        receiver: String
    ): Result<GlobalError, List<Chat>> {
        return withContext(Dispatchers.IO) {
            try {
                val chats = controller.getChatMessages(
                    sender = sender,
                    receiver = receiver
                ).map {
                    val user = userDao.getUser(it.sender)
                    it.toChat(user!!)
                }
                Result.Success(chats)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Failure(GlobalError.UnknownError)
            }
        }
    }
}