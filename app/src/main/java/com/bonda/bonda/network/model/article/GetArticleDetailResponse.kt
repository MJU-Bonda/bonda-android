package com.bonda.bonda.network.model.article

import kotlinx.serialization.Serializable

@Serializable
data class GetArticleDetailResponse(
    val articleId: Long,
    val title: String,
    val introduction: String,
    val content: String,
    val articleCategory: String,
    val isBookmarked: Boolean,
    val imageUrl: String,
    val isNewBadge: Boolean?,
    val relatedBookList: List<RelatedBookItem>,
    val otherArticleList: List<OtherArticleItem>
) {
    @Serializable
    data class RelatedBookItem(
        val bookId: Long,
        val title: String,
        val author: String,
        val category: String,
        val introduction: String,
        val content: String,
        val imageUrl: String
    )

    @Serializable
    data class OtherArticleItem(
        val articleId: Long,
        val title: String,
        val articleCategory: String,
        val imageUrl: String
    )
}
