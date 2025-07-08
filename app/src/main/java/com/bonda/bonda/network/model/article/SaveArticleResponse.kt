package com.bonda.bonda.network.model.article

import kotlinx.serialization.Serializable

@Serializable
data class SaveArticleResponse(
    val articleId: Long,
    val message: Message,
    val isNewBadge: Boolean
) {
    @Serializable
    data class Message(
        val message: String
    )
}