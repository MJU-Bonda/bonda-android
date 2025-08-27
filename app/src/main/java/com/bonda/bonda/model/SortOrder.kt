package com.bonda.bonda.model

enum class SortOrder(val code: String, val label: String) {
    RECENTLY_SAVED("recentlysaved", "최근담은순"),
    TITLE("title", "제목"),
    RECENT("recent", "최신순"),
    NEWEST("newest", "최신순"),
    POPULARITY("popularity", "인기순")
}