package com.bonda.bonda.network.model.book

import kotlinx.serialization.Serializable

@Serializable
data class MostLovedBooksResponse(
    val subject: String,
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
