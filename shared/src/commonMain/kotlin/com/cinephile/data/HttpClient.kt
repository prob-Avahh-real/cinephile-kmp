// HttpClient.kt — 影迷 API 客户端 (Ktor, KMP common)
package com.cinephile.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class CinephileHttpClient private constructor() {
    @PublishedApi
    internal var authToken: String? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    val client = HttpClient {
        install(ContentNegotiation) {
            json(this@CinephileHttpClient.json)
        }

        defaultRequest {
            url("http://10.0.2.2:3000/api/v1/")
            contentType(ContentType.Application.Json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 15_000
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 2)
            retryOnException(maxRetries = 3)
            exponentialDelay()
        }
    }

    fun setToken(token: String?) { authToken = token }
    fun getToken(): String? = authToken
    val isLoggedIn: Boolean get() = authToken != null
    fun logout() { authToken = null }

    suspend inline fun <reified T> get(path: String, params: Map<String, String> = emptyMap()): T {
        return client.get(path) {
            authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            params.forEach { (k, v) -> parameter(k, v) }
        }.body()
    }

    suspend inline fun <reified T> post(path: String, body: Any? = null): T {
        return client.post(path) {
            authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            if (body != null) setBody(body)
        }.body()
    }

    suspend inline fun <reified T> put(path: String, body: Any? = null): T {
        return client.put(path) {
            authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            if (body != null) setBody(body)
        }.body()
    }

    suspend inline fun <reified T> delete(path: String): T {
        return client.delete(path) {
            authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun warmup() {
        try {
            client.get("/health").body<Unit>()
        } catch (_: Exception) {
            // warming up only — errors expected
        }
    }

    companion object {
        val shared: CinephileHttpClient by lazy { CinephileHttpClient() }
    }
}

class ApiException(val statusCode: Int, message: String) : Exception(message) {
    override fun toString(): String = "ApiException($statusCode): $message"
}
