package com.blastclouddi

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object CheckResultRepository {

    private val inMemoryResults = mutableListOf<CheckResult>()
    private var useDatabase = false

    fun enableDatabase() {
        useDatabase = true
    }

    fun save(result: CheckResult) {
        if (useDatabase) {
            transaction {
                CheckResults.insert {
                    it[targetId] = result.targetId
                    it[checkedAt] = LocalDateTime.parse(result.checkedAt)
                    it[isUp] = result.isUp
                    it[responseTimeMs] = result.responseTimeMs
                    it[statusCode] = result.statusCode
                    it[errorMessage] = result.errorMessage
                }
            }
        } else {
            inMemoryResults.add(result)
        }
    }

    fun getHistoryForTarget(targetId: String, limit: Int = 50): List<CheckResult> {
        return if (useDatabase) {
            transaction {
                CheckResults.selectAll()
                    .where { CheckResults.targetId eq targetId }
                    .orderBy(CheckResults.checkedAt, SortOrder.DESC)
                    .limit(limit)
                    .map { row ->
                        CheckResult(
                            id = row[CheckResults.id],
                            targetId = row[CheckResults.targetId],
                            checkedAt = row[CheckResults.checkedAt].toString(),
                            isUp = row[CheckResults.isUp],
                            responseTimeMs = row[CheckResults.responseTimeMs],
                            statusCode = row[CheckResults.statusCode],
                            errorMessage = row[CheckResults.errorMessage]
                        )
                    }
            }
        } else {
            inMemoryResults
                .filter { it.targetId == targetId }
                .sortedByDescending { it.checkedAt }
                .take(limit)
        }
    }

    fun clear() {
        if (!useDatabase) {
            inMemoryResults.clear()
        }
    }
}