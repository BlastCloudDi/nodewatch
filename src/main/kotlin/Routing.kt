package com.blastclouddi

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val status: String)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }

        get("/health") {
            call.respond(HealthResponse(status = "ok"))
        }

        route("/targets") {
            get {
                call.respond(TargetRepository.getAll())
            }

            post {
                val request = call.receive<CreateTargetRequest>()

                if (request.name.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Target name must not be blank")
                    )
                    return@post
                }

                if (request.url.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Target url must not be blank")
                    )
                    return@post
                }

                if (request.checkIntervalSeconds <= 0) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("checkIntervalSeconds must be greater than 0")
                    )
                    return@post
                }

                val target = Target(
                    name = request.name.trim(),
                    url = request.url.trim(),
                    checkIntervalSeconds = request.checkIntervalSeconds
                )

                TargetRepository.add(target)

                call.respond(HttpStatusCode.Created, target)
            }

            get("/{id}") {
                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Missing target id")
                    )
                    return@get
                }

                val target = TargetRepository.findById(id)

                if (target == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("Target not found")
                    )
                    return@get
                }

                call.respond(target)
            }

            delete("/{id}") {
                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Missing target id")
                    )
                    return@delete
                }

                val removed = TargetRepository.remove(id)

                if (removed) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ErrorResponse("Target not found")
                    )
                }
            }
        }
    }
}