package com.arnav

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabase() {
    println("Configuring DB Connection")
    val config = HikariConfig().apply {
        jdbcUrl = System.getenv("JDBC_DATABASE_URL")
        driverClassName = System.getenv("JDBC_DRIVER")
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
            exposedLogger.info("Database health check successful: current time is $result")
            true
        }
    } catch (e: Exception) {
        // Log the exception to see what went wrong
        exposedLogger.error("Database connection failed!", e)
        false
    }
}