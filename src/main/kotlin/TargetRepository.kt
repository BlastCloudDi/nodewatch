package com.blastclouddi

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

object TargetRepository {
    private val inMemoryTargets = mutableListOf<Target>()
    private var useDatabase = false

    fun enableDatabase() {
        useDatabase = true
    }

    fun getAll(): List<Target> {
        return if (useDatabase) {
            transaction {
                Targets.selectAll().map { row ->
                    Target(
                        id = row[Targets.id],
                        name = row[Targets.name],
                        url = row[Targets.url],
                        checkIntervalSeconds = row[Targets.checkIntervalSeconds]
                    )
                }
            }
        } else {
            inMemoryTargets.toList()
        }
    }

    fun add(target: Target) {
        if (useDatabase) {
            transaction {
                Targets.insert {
                    it[id] = target.id
                    it[name] = target.name
                    it[url] = target.url
                    it[checkIntervalSeconds] = target.checkIntervalSeconds
                }
            }
        } else {
            inMemoryTargets.add(target)
        }
    }

    fun remove(id: String): Boolean {
        return if (useDatabase) {
            transaction {
                Targets.deleteWhere { Targets.id eq id } > 0
            }
        } else {
            inMemoryTargets.removeIf { it.id == id }
        }
    }

    fun findById(id: String): Target? {
        return if (useDatabase) {
            transaction {
                Targets.selectAll().where { Targets.id eq id }
                    .map { row ->
                        Target(
                            id = row[Targets.id],
                            name = row[Targets.name],
                            url = row[Targets.url],
                            checkIntervalSeconds = row[Targets.checkIntervalSeconds]
                        )
                    }
                    .singleOrNull()
            }
        } else {
            inMemoryTargets.find { it.id == id }
        }
    }

    fun clear() {
        if (useDatabase) {
            transaction {
                Targets.deleteAll()
            }
        } else {
            inMemoryTargets.clear()
        }
    }
}