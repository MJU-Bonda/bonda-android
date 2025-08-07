package com.bonda.bonda.ui.home.articles

data class Article(
    val id: Long,
    val isSaved: Boolean,
    val coverImage: String,
    val category: String,
    val title: String,
    val subTitle: String
)
