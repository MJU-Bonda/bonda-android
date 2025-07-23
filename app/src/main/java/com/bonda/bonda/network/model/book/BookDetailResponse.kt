package com.bonda.bonda.network.model.book

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookDetailResponse(
    val bookId: Long,
    val isBookmarked: Boolean,
    val category: String,
    val title: String,
    val imageUrl: String,
    val author: String,
    val publisher: String,
    val plateType: String,
    val page: Int,
    val subject: String?,
    val introduction: String?,
    val content: String,
    val isNewBadge: Boolean,
    @SerialName("related_article_list")
    val relatedArticleList: List<RelatedArticle>
) {
    @Serializable
    data class RelatedArticle(
        val articleId: Long,
        val title: String,
        val articleCategory: String,
        val imageUrl: String
    )
}
