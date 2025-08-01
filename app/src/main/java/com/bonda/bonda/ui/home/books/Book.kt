package com.bonda.bonda.ui.home.books

data class Book(
    val id: Long,
    val coverImage: String,
    val category: String,
    val title: String,
    val author: String,
)