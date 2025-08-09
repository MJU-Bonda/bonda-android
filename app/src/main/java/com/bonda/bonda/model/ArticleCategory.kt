package com.bonda.bonda.model

enum class ArticleCategory(val code: String, val label: String) {
    ALL("ALL", "전체"),
    AUTHOR_OR_PUBLISHER("AUTHOR_OR_PUBLISHER", "작가/출판사"),
    BOOKSTORE("BOOKSTORE", "독립서점"),
    THEME("THEME", "테마");

    companion object {
        val BUSINESS_CATEGORIES: List<ArticleCategory> = entries.filter { it != ALL }
    }
}

fun String.toArticleCategory(): ArticleCategory =
    ArticleCategory.entries.find { it.code == this } ?: ArticleCategory.ALL