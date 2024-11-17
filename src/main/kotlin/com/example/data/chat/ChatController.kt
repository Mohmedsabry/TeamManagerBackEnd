package com.example.data.chat

import com.example.data.db.UserDao
import com.example.data.dto.MessageDto
import com.example.data.mapper.toChat
import com.example.data.mapper.toDto
import com.example.data.mongodb.controller.Controller
import com.example.domain.model.Chat
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val dao: Controller,
    private val userDao: UserDao,
) {
    val members = ConcurrentHashMap<String, ChatMember>()
    fun join(
        sender: String,
        receiver: String,
        sessionId: String,
        socketSession: WebSocketSession
    ) {
        if (sessionId.isBlank() && members.containsKey(sessionId)) {
            return
        }
        members[sessionId] = ChatMember(sender, receiver, sessionId, socketSession)
    }

    suspend fun disConnection(sessionId: String) {
        if (members.containsKey(sessionId)) {
            members[sessionId]?.socket?.close()
            members.remove(sessionId)
        }
    }

    suspend fun sendMessage(
        sessionId: String,
        content: String,
        sender: String,
        receiver: String
    ) {
        try {
            dao.sendMessage(
                sender = sender,
                receiver = receiver,
                message = content
            )
            val user = userDao.getUser(sender) ?: return
            val dto = MessageDto(
                user = user.toDto().copy(
                    image = if (user.image != "no image") File(user.image).readBytes()
                        .encodeBase64() else user.image
                ),
                message = content,
                time = System.currentTimeMillis()
            )
            val encodedMessage = Json.encodeToString(dto)
            members.values.forEach { member ->
                if (
                    (member.sender == sender && member.receiver == receiver) ||
                    (member.sender == receiver && member.receiver == sender)
                ) {
                    member.socket.send(Frame.Text(encodedMessage))
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun getAllMessage(sender: String, receiver: String): List<Chat> {
        return coroutineScope {
            dao.getChatMessages(sender, receiver).map {
                async {
                    val user = userDao.getUser(it.sender)
                    it.toChat(user!!)
                }
            }.awaitAll()
        }
    }
}