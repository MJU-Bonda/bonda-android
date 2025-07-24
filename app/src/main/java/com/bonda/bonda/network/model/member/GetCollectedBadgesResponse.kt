package com.bonda.bonda.network.model.member

import kotlinx.serialization.Serializable

@Serializable
data class GetCollectedBadgesResponse (
    val badgeCount: Int,
    val viewBadgeList: List<Badge>,
    val saveBadgeList: List<Badge>
) {
    @Serializable
    data class Badge(
        val name: String,
        val isUnlocked: Boolean,
        val id: Int
    )
}
