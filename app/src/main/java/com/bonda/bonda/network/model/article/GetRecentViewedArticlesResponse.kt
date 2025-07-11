package com.bonda.bonda.network.model.article

import kotlinx.serialization.Serializable

@Serializable
data class GetRecentViewedArticlesResponse(
    val page: Int,
    val hasNextPage: Boolean,
    val articleList: List<Article>
) {
    @Serializable
    data class Article(
        val articleId: Long,
        val title: String,
        val articleCategory: String,
        val imageUrl: String
    )
}
