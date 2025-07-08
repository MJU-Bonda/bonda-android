package com.bonda.bonda.network.model.article

import kotlinx.serialization.Serializable

@Serializable
data class GetSavedArticlesResponse(
    val page: Int,
    val total: Int,
    val orderBy: String,
    val hasNextPage: Boolean,
    val articleList: List<SearchArticleItem>
) {
    @Serializable
    data class SearchArticleItem(
        val articleId: Long,
        val title: String,
        val articleCategory: String,
        val imageUrl: String
    )
}
