package com.bonda.bonda.network.model.member

import kotlinx.serialization.Serializable

@Serializable
data class GetBadgeDetailResponse (
    val name: String,
    val description: String,
    val progressType: String,
    val currentProgress: Int,
    val goal: Int,
    val isUnlocked: Boolean,
    val acquiredDate: String?
)
