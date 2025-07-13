package com.bonda.bonda.network.model.article

import kotlinx.serialization.Serializable

@Serializable
data class SaveArticleResponse(
    val articleId: Long,
    val message: String,
    val isNewBadge: Boolean
)
