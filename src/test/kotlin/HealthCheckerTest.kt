package com.blastclouddi

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class HealthCheckerTest {

    @Test
    fun `check returns UP for healthy target`() = runBlocking {
        val originalClient = HealthChecker.client
        HealthChecker.client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respond(
                        content = "OK",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "text/plain")
                    )
                }
            }
        }

        try {
            val target = Target(name = "Test", url = "http://example.com")
            val result = HealthChecker.check(target)

            assertTrue(result.isUp)
            assertEquals(200, result.statusCode)
            assertNotNull(result.responseTimeMs)
            assertNull(result.errorMessage)
        } finally {
            HealthChecker.client.close()
            HealthChecker.client = originalClient
        }
    }

    @Test
    fun `check returns DOWN for 500 error`() = runBlocking {
        val originalClient = HealthChecker.client
        HealthChecker.client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    respond(
                        content = "Internal Server Error",
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, "text/plain")
                    )
                }
            }
        }

        try {
            val target = Target(name = "Test", url = "http://example.com")
            val result = HealthChecker.check(target)

            assertFalse(result.isUp)
            assertEquals(500, result.statusCode)
            assertNull(result.errorMessage)
        } finally {
            HealthChecker.client.close()
            HealthChecker.client = originalClient
        }
    }

    @Test
    fun `check returns DOWN for unreachable target`() = runBlocking {
        val originalClient = HealthChecker.client
        HealthChecker.client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    throw java.io.IOException("Connection refused")
                }
            }
        }

        try {
            val target = Target(name = "Test", url = "http://example.com")
            val result = HealthChecker.check(target)

            assertFalse(result.isUp)
            assertNull(result.statusCode)
            assertNotNull(result.errorMessage)
            assertTrue(result.errorMessage.contains("Connection refused"))
        } finally {
            HealthChecker.client.close()
            HealthChecker.client = originalClient
        }
    }
}