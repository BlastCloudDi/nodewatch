package com.blastclouddi

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistoryEndpointTest {

    @Test
    fun `get history returns empty list for new target`() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }

        TargetRepository.clear()
        CheckResultRepository.clear()

        // Создаём цель
        val createResponse = client.post("/targets") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Test","url":"http://example.com","checkIntervalSeconds":60}""")
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)

        // Извлекаем ID из ответа
        val body = createResponse.bodyAsText()
        val idMatch = Regex("\"id\":\"([^\"]+)\"").find(body)
        val targetId = idMatch?.groupValues?.get(1) ?: throw AssertionError("ID not found in response")

        // Запрашиваем историю
        val historyResponse = client.get("/targets/$targetId/history")

        assertEquals(HttpStatusCode.OK, historyResponse.status)
        assertEquals("[]", historyResponse.bodyAsText())
    }

    @Test
    fun `get history returns 404 for non-existent target`() = testApplication {
        application {
            configureSerialization()
            configureRouting()
        }

        val response = client.get("/targets/non-existent-id/history")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Target not found"))
    }
}