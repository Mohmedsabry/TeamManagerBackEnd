package com.example.plugins

import com.example.data.dto.RequestPasswordDto
import com.example.data.dto.TaskDto
import com.example.data.dto.TeamDto
import com.example.data.dto.UserDto
import com.example.data.mapper.toDto
import com.example.data.mongodb.util.RequestStatue
import com.example.data.mongodb.util.TaskStatue
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.Repository
import com.example.util.Result
import com.example.util.save
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import java.io.File
import java.util.*


fun Application.configureAuthRouting(
    authRepository: AuthRepository,
) {
    routing {
        post("/auth") {
            var user: UserDto? = null
            val part = call.receiveMultipart()
            part.forEachPart {
                if (it is PartData.FileItem) {
                    println("file")
                    val file = it.save("${user!!.email}.img")
                    user = user!!.copy(image = file.path)
                }
                if (it is PartData.FormItem) {
                    user = Json.decodeFromString(it.value)
                    println("user $user")
                }
                it.dispose()
            }
            println("final $user")
            when (val res = authRepository.signup(user!!.toUser())) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.InternalServerError, res.error.name)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
        get("/auth") {
            val email = call.request.queryParameters["email"]
            val password = call.request.queryParameters["password"]
            if (email == null || password == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (val res = authRepository.login(email, password)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.InternalServerError, res.error.name)
                }

                is Result.Success -> {
                    call.respond(
                        HttpStatusCode.OK,
                        res.data.toDto().copy(
                            image = if (res.data.image != "no image") File(res.data.image).readBytes()
                                .encodeBase64() else res.data.image
                        )
                    )
                }
            }
        }
        post("/forgetPassword") {
            val email = call.request.queryParameters["email"]
            if (email == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val restCode = UUID.randomUUID().toString().take(6)
            when (val res = authRepository.forgotPassword(email, restCode)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.InternalServerError, res.error.name)
                }

                is Result.Success -> call.respond(HttpStatusCode.OK, "Check your mail")
            }
        }
        post("/checkCode") {
            val req = call.receive<RequestPasswordDto>()
            when (val result = authRepository.checkCode(sender = req.email, restCode = req.restCode)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.BadRequest, result.error.name)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, "update it")
                }
            }
        }
        get("/updatePassword") {
            val email = call.request.queryParameters["email"]
            val password = call.request.queryParameters["password"]
            if (email == null || password == null) {
                call.respond(HttpStatusCode.BadRequest, "email or password is required")
            }
            when (val res = authRepository.updatePassword(email = email!!, newPassword = password!!)) {
                is Result.Failure -> {
                    call.respond(HttpStatusCode.InternalServerError, res.error.name)
                }

                is Result.Success -> {
                    call.respond(HttpStatusCode.OK, res.data.toDto())
                }
            }
        }
    }
}

fun Application.configureMainRouting(repo: Repository) {
    routing {
        get("/member") {
            val email = call.request.queryParameters["email"] ?: ""
            when (val res = repo.getMember(email)) {
                is Result.Failure -> call.respond(HttpStatusCode.BadRequest, res.error.name)
                is Result.Success -> call.respond(
                    res.data.toDto().copy(
                        image = if (res.data.image != "no image") File(res.data.image).readBytes()
                            .encodeBase64() else res.data.image
                    )
                )
            }
        }
        post("/member") {
            val email = call.request.queryParameters["email"] ?: ""
            val teamId = call.request.queryParameters["teamId"] ?: ""
            when (val res = repo.addMemberToTeam(memberEmail = email, teamId = teamId)) {
                is Result.Failure -> call.respond(HttpStatusCode.BadRequest, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        get("/members") {
            when (val res = repo.getAllMember()) {
                is Result.Failure -> call.respond(HttpStatusCode.Conflict, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK, res.data.map {
                    it.toDto().copy(
                        image = if (it.image != "no image") File(it.image).readBytes()
                            .encodeBase64() else it.image
                    )
                })
            }
        }
        post("/task") {
            val task = call.receive<TaskDto>()
            when (val res = repo.addTask(task.toTask())) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        put("/task") {
            val due = call.request.queryParameters["due"]
            val statue = call.request.queryParameters["statue"].toBoolean()
            if (due == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val res = repo.updateTaskStatue(if (statue) TaskStatue.DONE else TaskStatue.IN_PROGRESS, due)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        delete("/task") {
            val task = call.receive<TaskDto>()
            when (val res = repo.deleteTask(task.toTask())) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        post("/team") {
            val teamDto = call.receive<TeamDto>()
            when (val res = repo.addTeam(teamDto.toTeam())) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK, res.data)
            }
        }
        get("/team") {
            val teamId = call.queryParameters["teamId"] ?: ""
            when (val result = repo.getTeam(teamId)) {
                is Result.Failure -> call.respond(HttpStatusCode.BadRequest, result.error.name)
                is Result.Success -> call.respond(result.data.toDto().copy(
                    members = result.data.toDto().members.map {
                        it.copy(
                            image = if (it.image != "no image") File(it.image).readBytes()
                                .encodeBase64() else it.image
                        )
                    }
                ))
            }
        }
        delete("/team") {
            val teamId = call.request.queryParameters["teamId"]
            if (teamId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            when (val res = repo.deleteTeam(teamId)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK, teamId)
            }
        }
        get("/team/{teamId}") {
            val teamId = call.parameters["teamId"]
            if (teamId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (val res = repo.getAllMemberForTeam(teamId)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK, res.data.map {
                    it.toDto().copy(
                        image = if (it.image != "no image") File(it.image).readBytes()
                            .encodeBase64() else it.image
                    )
                })
            }
        }
        get("/members/{memberId}") {
            val memberId = call.parameters["memberId"]
            if (memberId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (val result = repo.getAllTeamsForMember(memberId)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, result.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK, result.data.map {
                    it.toDto()
                })
            }
        }
        get("/task/{email}") {
            val due = call.parameters["email"]
            if (due == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (val res = repo.getAllTasksForMember(due)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK, res.data.map { it.toDto() })
            }
        }
        get("/task/{teamId}") {
            val teamId = call.parameters["teamId"]
            if (teamId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (val res = repo.getAllTasksForTeam(teamId)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK, res.data.map { it.toDto() })
            }
        }
        post("/join") {
            val teamId = call.request.queryParameters["teamId"]
            val email = call.request.queryParameters["memberEmail"]
            val admin = call.request.queryParameters["admin"]
            if (email == null || teamId == null || admin == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val res = repo.addMemberToTeam(teamId, email, admin)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        delete("/teamMember") {
            val teamId = call.request.queryParameters["teamId"]
            val email = call.request.queryParameters["email"]
            if (email == null || teamId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            when (val res = repo.deleteMemberFromTeam(teamId, email)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        get("/request") {
            val email = call.request.queryParameters["email"]
            if (email == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (val res = repo.getAllRequestsForMember(memberEmail = email)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK,
                    res.data.filter { it.statues == RequestStatue.PENDING.name.lowercase() }.map { it.toDto() })
            }
        }
        put("/request/{id}") {
            val id = call.parameters["id"]
            val statue = call.request.queryParameters["statue"]
            var statues = RequestStatue.ACCEPTED
            if (statue == null || id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            if (statue == "rejected") statues = RequestStatue.REJECTED
            when (val res = repo.updateRequestStatue(statues, id)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        get("/notification") {
            val email = call.request.queryParameters["email"]
            if (email == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (val res = repo.getNotification(email)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK, res.data.map { it.toDto() })
            }
        }
        delete("/notification") {
            val email = call.request.queryParameters["email"]
            if (email == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            when (val res = repo.clearNotification(email)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK)
            }
        }
        get("/messages") {
            val sender = call.request.queryParameters["sender"]
            val receiver = call.request.queryParameters["receiver"]
            if (sender == null || receiver == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (val res = repo.getChatMessages(sender, receiver)) {
                is Result.Failure -> call.respond(HttpStatusCode.InternalServerError, res.error.name)
                is Result.Success -> call.respond(HttpStatusCode.OK, res.data.map {
                    it.toDto().copy(
                        user = it.user.toDto().copy(
                            image = if (it.user.image != "no image") File(it.user.image).readBytes()
                                .encodeBase64() else it.user.image
                        )
                    )
                })
            }
        }
    }
}