package com.arnav

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            println("Enabled GSON & Pretty Print")
            setPrettyPrinting()
        }
    }
}
