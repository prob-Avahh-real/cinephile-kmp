// AuthRepository.kt — 认证仓库 (KMP)
package com.cinephile.data

import com.cinephile.model.AuthResponse
import com.cinephile.model.AuthUser
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class AuthRepository(private val client: CinephileHttpClient = CinephileHttpClient.shared) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun login(email: String, password: String): AuthResponse {
        val response = client.post<JsonObject>("/auth/login", mapOf("email" to email, "password" to password))
        val authResponse = json.decodeFromJsonElement<AuthResponse>(response)
        client.setToken(authResponse.token)
        return authResponse
    }

    suspend fun register(email: String, password: String, nickname: String? = null): AuthResponse {
        val body = buildMap {
            put("email", email)
            put("password", password)
            if (nickname != null) put("nickname", nickname)
        }
        val response = client.post<JsonObject>("/auth/register", body)
        val authResponse = json.decodeFromJsonElement<AuthResponse>(response)
        client.setToken(authResponse.token)
        return authResponse
    }

    suspend fun me(): AuthUser {
        val response = client.get<JsonObject>("/auth/me")
        return json.decodeFromJsonElement(response)
    }

    suspend fun updateProfile(nickname: String? = null, avatarUrl: String? = null): AuthUser {
        val body = buildMap {
            if (nickname != null) put("nickname", nickname)
            if (avatarUrl != null) put("avatar_url", avatarUrl)
        }
        val response = client.put<JsonObject>("/auth/me", body)
        return json.decodeFromJsonElement(response)
    }
}
