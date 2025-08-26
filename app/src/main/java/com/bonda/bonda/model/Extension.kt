package com.bonda.bonda.model

fun String.toArticleCategory(): ArticleCategory =
    ArticleCategory.entries.find { it.code == this } ?: ArticleCategory.ALL

fun String.toBookCategory(): BookCategory =
    BookCategory.entries.find { it.code == this } ?: BookCategory.ALL

fun String.toBookTheme(): BookTheme =
    BookTheme.entries.find { it.code == this } ?: BookTheme.ALL

fun String.toSortOrder(): SortOrder =
    SortOrder.entries.find { it.code == this } ?: SortOrder.RECENTLY_SAVED