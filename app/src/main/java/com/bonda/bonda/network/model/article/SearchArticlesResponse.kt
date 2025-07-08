package com.bonda.bonda.network.model.article

import kotlinx.serialization.Serializable

@Serializable
data class SearchArticlesResponse(
    val page: Int,
    val total: Int,
    val word: String,
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
