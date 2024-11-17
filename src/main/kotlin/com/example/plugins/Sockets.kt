package com.example.plugins

import com.example.data.chat.ChatController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Application.configureSockets(chatController: ChatController) {
    install(WebSockets) {
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/chat") {
            val session = call.sessions.get<MySession>()
            if (session == null) {
                close()
                return@webSocket
            }
            val sender = session.sender
            val receiver = session.receiver
            val sessionId = session.sessionId
            try {
                chatController.join(
                    sender = sender,
                    receiver = receiver,
                    sessionId = sessionId,
                    socketSession = this
                )
                incoming.consumeEach { frame: Frame ->
                    if (frame is Frame.Text) {
                        chatController.sendMessage(
                            sessionId = sessionId,
                            content = frame.readText(),
                            sender = sender,
                            receiver = receiver,
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest)
            } finally {
                chatController.disConnection(sessionId = sessionId)
            }
        }
    }
}
