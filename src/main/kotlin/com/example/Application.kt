package com.example

import com.example.data.chat.ChatController
import com.example.di.mainModule
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    configureSecurity()
    configureSerialization()
    configureSockets(ChatController(get(), get()))
    configureAuthRouting(get())
    configureMainRouting(get())
}
