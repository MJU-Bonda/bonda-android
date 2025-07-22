package com.bonda.bonda.network.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchArticlesResponse (
    val page: Int,
    val total: Int,
    val word: String,
    val orderBy: String,
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
