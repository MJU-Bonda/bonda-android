package com.bonda.bonda.model

enum class BookTheme(val code: String, val label: String) {
    ALL("ALL", "전체"),
    COOKING("COOKING", "음식/요리"),
    SELF_DEVELOPMENT("SELF_DEVELOPMENT", "자기계발"),
    ART("ART", "예술"),
    SPACE("SPACE", "공간"),
    MOVIE("MOVIE", "영화"),
    PET("PET", "반려동물"),
    LOVE("LOVE", "사랑"),
    COFFEE("COFFEE", "커피"),
    COMFORT("COMFORT", "위로"),
    PLANT("PLANT", "자연/식물"),
    BURNOUT("BURNOUT", "번아웃"),
    MUSIC("MUSIC", "음악");

    companion object {
        val BUSINESS_THEMES: List<BookTheme> = entries.filter { it != ALL }
    }
}