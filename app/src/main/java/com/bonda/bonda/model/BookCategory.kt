package com.bonda.bonda.model

enum class BookCategory(val code: String, val label: String) {
    ALL("ALL", "전체"),
    POEM("POEM", "시집"),
    NOVEL("NOVEL", "소설"),
    ESSAY("ESSAY", "에세이"),
    CARTOON("CARTOON", "만화"),
    PHOTO_BOOK("PHOTO_BOOK", "사진집"),
    ART_BOOK("ART_BOOK", "아트북"),
    ILLUSTRATION("ILLUSTRATION", "일러스트집"),
    MAGAZINE("MAGAZINE", "매거진");

    companion object {
        val BUSINESS_CATEGORIES: List<BookCategory> = entries.filter { it != ALL }
    }
}

fun String.toBookCategory(): BookCategory =
    BookCategory.entries.find { it.code == this } ?: BookCategory.ALL