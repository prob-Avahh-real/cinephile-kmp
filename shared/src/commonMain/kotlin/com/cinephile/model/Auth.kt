// Auth.kt — 影迷认证模型
package com.cinephile.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthUser(
    val id: String,
    val email: String,
    val nickname: String? = null,
    val avatarUrl: String? = null
)

@Serializable
data class AuthResponse(
    val user: AuthUser,
    val token: String
)
