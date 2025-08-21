package com.bonda.bonda.model

enum class SortOrder(val code: String, val label: String) {
    RECENT("recentlysaved", "최근담은순"),
    TITLE("title", "제목")
}

fun String.toSortOrder(): SortOrder =
    SortOrder.entries.find { it.code == this } ?: SortOrder.RECENT
