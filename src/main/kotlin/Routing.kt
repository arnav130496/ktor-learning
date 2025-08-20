package com.arnav

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Application.configureRouting() {
    routing {
        println("Enabled Routing")

        get("/") {
            call.respond(HttpStatusCode.OK,"Hello World!")
        }

        get("/json/gson") {
            call.respond(HttpStatusCode.OK,mapOf("hello" to "world"))
        }

        post(path = "/meter"){
            call.respond(status = HttpStatusCode.OK, Meter("id","Atlas",
                "project2","/api/v1","agent_123","gpt-4o",30,
                400, 170.5))
        }

        get("/health") {
            // Execute the database check on a separate thread to avoid blocking the main event loop
            val isConnected = withContext(Dispatchers.IO) {
                isDbConnected()
            }

            if (isConnected) {
                call.respond(HttpStatusCode.OK, "Database connection is healthy.")
            } else {
                call.respond(HttpStatusCode.ServiceUnavailable, "Database connection is unavailable.")
            }
        }
    }
}

data class Meter(
    val id: String,
    val source: String,
    val project: String,
    val endpoint: String,
    val agentId: String,
    val llmModel: String,
    val inputTokenCount: Int,
    val outputTokenCount: Int,
    val coinsCount: Double
)
