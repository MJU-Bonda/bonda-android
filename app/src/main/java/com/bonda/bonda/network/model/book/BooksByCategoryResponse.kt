package com.bonda.bonda.network.model.book

import kotlinx.serialization.Serializable

@Serializable
data class BooksByCategoryResponse(
    val page: Int,
    val total: Int,
    val category: String,
    val orderBy: String,
    val hasNextPage: String,
    val bookList: List<Book>,
) {
    @Serializable
    data class Book(
        val id: Long,
        val title: String,
        val author: String,
        val imageUrl: String,
        val category: String,
    )
}
