package com.bonda.bonda.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(
    val accessToken: String,
    val refreshToken: String
)
