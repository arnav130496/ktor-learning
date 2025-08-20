package com.arnav

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.OffsetDateTime

fun Application.configureDatabase() {
    println("Configuring DB Connection")
    val config = HikariConfig().apply {
        val databaseConfig = environment.config.config("ktor.database")

        // The property names now match your YAML structure
        jdbcUrl = databaseConfig.property("jdbcUrl").getString()
        driverClassName = databaseConfig.property("jdbcDriver").getString()
        username = databaseConfig.property("user").getString()
        password = databaseConfig.property("password").getString()

        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
    println("Database connection established.")
}

fun isDbConnected(): Boolean {
    return try {
        // Run a simple transaction to test the connection
        transaction {
            // A non-destructive query, like selecting the current time from the database
            val result = exec("SELECT cdf_service FROM service_plans where external_id='BASIC_TS_PLAN'; ") { rs ->
                rs.next()
                rs.getObject(1, String::class.java)
            }
            exposedLogger.info("Database health check successful: Dummy data fetched from service_plans table is $result")

            val resultTime = exec("SELECT now() ") { rs ->
                rs.next()
                rs.getObject(1, OffsetDateTime::class.java)
            }
            exposedLogger.info("Database health check successful: current time is $resultTime")
            true
        }
    } catch (e: Exception) {
        // Log the exception to see what went wrong
        exposedLogger.error("Database connection failed!", e)
        false
    }
}