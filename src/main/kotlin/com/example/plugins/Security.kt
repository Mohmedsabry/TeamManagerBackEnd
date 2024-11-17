package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.sessions.*
import io.ktor.util.*

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<MySession>("MY_SESSION")
    }
    intercept(Plugins) {
        if (call.sessions.get<MySession>() == null) {
            val sender = call.request.queryParameters["sender"] ?: ""
            val receiver = call.request.queryParameters["receiver"] ?: ""
            call.sessions.set(
                MySession(
                    sender = sender,
                    receiver = receiver,
                    sessionId = generateNonce()
                )
            )
        }
    }
}
