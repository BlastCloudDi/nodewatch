package com.blastclouddi

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TargetRoutesTest {

    @Test
    fun `get targets returns empty list`() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }
        TargetRepository.clear()

        val response = client.get("/targets")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @Test
    fun `post target creates target`() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }
        TargetRepository.clear()

        val response = client.post("/targets") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "name": "Local healthcheck",
                    "url": "http://localhost:8080/health",
                    "checkIntervalSeconds": 30
                }
                """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)

        val body = response.bodyAsText()

        assertTrue(body.contains("Local healthcheck"))
        assertTrue(body.contains("http://localhost:8080/health"))
        assertTrue(body.contains("checkIntervalSeconds"))
        assertTrue(body.contains("id"))
    }

    @Test
    fun `post target with invalid interval returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }
        TargetRepository.clear()

        val response = client.post("/targets") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                    "name": "Invalid target",
                    "url": "http://localhost:8080/health",
                    "checkIntervalSeconds": 0
                }
                """.trimIndent()
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("checkIntervalSeconds"))
    }
}