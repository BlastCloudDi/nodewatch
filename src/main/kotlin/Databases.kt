package com.blastclouddi

import io.ktor.server.application.Application

fun Application.configureDatabases() {
    DatabaseFactory.init()
}