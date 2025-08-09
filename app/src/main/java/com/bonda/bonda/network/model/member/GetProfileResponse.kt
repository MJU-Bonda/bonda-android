package com.bonda.bonda.network.model.member

import kotlinx.serialization.Serializable

@Serializable
data class GetProfileResponse(
    val nickname: String,
    val profileImage: String?,
    val savedBookCount: Int,
    val badgeCount: Int
)
