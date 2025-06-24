package com.bonda.bonda.network.model.member

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val member: String,
    val nickname: String?,
    val profileImage: String?
)
