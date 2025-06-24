package com.bonda.bonda.network.model.member

import kotlinx.serialization.Serializable

@Serializable
data class GetProfileRequest(
    val member: String
)
