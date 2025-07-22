package com.bonda.bonda.network.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchBooksResponse (
    val page: Int,
    val total: Int,
    val word: String,
    val orderBy: String,
    val hasNextPage: Boolean,
    val bookList: List<Book>
) {
    @Serializable
    data class Book(
        val id: Long,
        val title: String,
        val author: String,
        val imageUrl: String,
        val category: String
    )
}
