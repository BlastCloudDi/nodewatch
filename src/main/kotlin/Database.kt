package com.blastclouddi

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

// Описание таблицы targets
object Targets : Table("targets") {
    val id = varchar("id", 36)
    val name = varchar("name", 255)
    val url = varchar("url", 2048)
    val checkIntervalSeconds = integer("check_interval_seconds")

    override val primaryKey = PrimaryKey(id)
}

object DatabaseFactory {
    fun init() {
        val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/nodewatch"
        val dbUser = System.getenv("DB_USER") ?: "nodewatch"
        val dbPassword = System.getenv("DB_PASSWORD") ?: "nodewatch"

        val config = HikariConfig().apply {
            jdbcUrl = dbUrl
            username = dbUser
            password = dbPassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        // Создаём таблицу, если её нет
        transaction {
            SchemaUtils.create(Targets)
        }

        TargetRepository.enableDatabase()
    }
}