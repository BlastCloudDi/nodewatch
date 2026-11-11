package com.blastclouddi

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.withTimeout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HealthChecker {

    var client: HttpClient = HttpClient(CIO)

    //Проверяет доступность цели и возвращает результат
    suspend fun check(target: Target): CheckResult {
        val startTime = System.currentTimeMillis()
        val checkedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        return try {
            // Ограничиваем время ожидания 10 секундами
            val response = withTimeout(10_000) {
                client.get(target.url)
            }

            val responseTime = System.currentTimeMillis() - startTime

            CheckResult(
                targetId = target.id,
                checkedAt = checkedAt,
                isUp = response.status.value in 200..299,
                responseTimeMs = responseTime,
                statusCode = response.status.value,
                errorMessage = null
            )
        } catch (e: Exception) {
            val responseTime = System.currentTimeMillis() - startTime

            CheckResult(
                targetId = target.id,
                checkedAt = checkedAt,
                isUp = false,
                responseTimeMs = responseTime,
                statusCode = null,
                errorMessage = e.message?.take(1000) // Ограничиваем длину ошибки
            )
        }
    }

    fun close() {
        client.close()
    }
}