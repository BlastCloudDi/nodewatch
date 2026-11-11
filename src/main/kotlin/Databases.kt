package com.blastclouddi

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped

fun Application.configureDatabases() {
    DatabaseFactory.init()

    // Запускаем фоновый worker при старте приложения
    monitor.subscribe(ApplicationStarted) {
        BackgroundWorker.start()
    }

    // Останавливаем worker при остановке приложения
    monitor.subscribe(ApplicationStopped) {
        BackgroundWorker.stop()
    }
}