// AuthRepository.kt — 认证仓库 (KMP)
package com.cinephile.data

import com.cinephile.model.AuthResponse
import com.cinephile.model.AuthUser

class AuthRepository(private val client: CinephileHttpClient = CinephileHttpClient.shared) {

    suspend fun login(email: String, password: String): AuthResponse {
        val authResponse = client.post<AuthResponse>(
            "/auth/login",
            mapOf("email" to email, "password" to password),
        )
        client.setToken(authResponse.token)
        return authResponse
    }

    suspend fun register(email: String, password: String, nickname: String? = null): AuthResponse {
        val body = buildMap {
            put("email", email)
            put("password", password)
            if (nickname != null) put("nickname", nickname)
        }
        val authResponse = client.post<AuthResponse>("/auth/register", body)
        client.setToken(authResponse.token)
        return authResponse
    }

    suspend fun me(): AuthUser = client.get("/auth/me")

    suspend fun updateProfile(nickname: String? = null, avatarUrl: String? = null): AuthUser {
        val body = buildMap {
            if (nickname != null) put("nickname", nickname)
            if (avatarUrl != null) put("avatar_url", avatarUrl)
        }
        return client.put("/auth/me", body)
    }
}
