package com.blastclouddi

import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object BackgroundWorker {

    private var job: Job? = null


    // Запускает фоновую проверку всех целей
    fun start() {
        if (job != null && job!!.isActive) {
            return
        }

        // SupervisorJob — если одна проверка упадёт, другие продолжат работать
        job = CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            while (isActive) {
                try {
                    checkAllTargets()
                } catch (e: Exception) {
                    println("Error in background worker: ${e.message}")
                }

                // Ждём 60 секунд перед следующей проверкой
                delay(60_000)
            }
        }
    }

    // Останавливает фоновую проверку
    fun stop() {
        job?.cancel()
        job = null
        HealthChecker.close()
    }

    // Проверяет все цели, у которых наступило время проверки
    private suspend fun checkAllTargets() {
        val targets = TargetRepository.getAll()
        val now = LocalDateTime.now()

        // Запускаем проверки параллельно
        val jobs = targets.map { target ->
            CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
                try {
                    // Получаем последнюю проверку для этой цели
                    val history = CheckResultRepository.getHistoryForTarget(target.id, limit = 1)
                    val lastCheck = history.firstOrNull()

                    // Проверяем, нужно ли пинговать эту цель
                    val shouldCheck = if (lastCheck == null) {
                        true
                    } else {
                        val lastCheckTime = LocalDateTime.parse(lastCheck.checkedAt)
                        val minutesSinceLastCheck = ChronoUnit.MINUTES.between(lastCheckTime, now)
                        minutesSinceLastCheck >= target.checkIntervalSeconds / 60
                    }

                    if (shouldCheck) {
                        val result = HealthChecker.check(target)
                        CheckResultRepository.save(result)
                        println("Checked ${target.name}: ${if (result.isUp) "UP" else "DOWN"} (${result.responseTimeMs}ms)")
                    }
                } catch (e: Exception) {
                    println("Error checking target ${target.name}: ${e.message}")
                }
            }
        }

        // Ждём завершения всех проверок
        jobs.joinAll()
    }
}