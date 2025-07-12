package com.bonda.bonda.network.model.article

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetArticlesResponse(
    val page: Int,
    val category: String,
    val hasNextPage: Boolean,
    val articleList: List<ArticleItem>
) {
    @Serializable
    data class ArticleItem(
        val articleId: Long,
        val category: String,
        val title: String,
        val introduction: String,
        val imageUrl: String,
        val isBookmarked: Boolean
    )
}
