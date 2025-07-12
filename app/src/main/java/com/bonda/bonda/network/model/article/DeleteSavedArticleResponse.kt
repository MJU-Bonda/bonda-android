package com.bonda.bonda.network.model.article

import kotlinx.serialization.Serializable

@Serializable
data class DeleteSavedArticleResponse(
    val articleId: Long,
    val message: Message,
) {
    @Serializable
    data class Message(
        val message: String
    )
}
