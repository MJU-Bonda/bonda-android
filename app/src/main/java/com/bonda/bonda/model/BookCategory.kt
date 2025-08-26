package com.bonda.bonda.model

import androidx.annotation.DrawableRes
import com.bonda.bonda.R

enum class BookCategory(
    val code: String,
    val label: String,
    @DrawableRes val iconRes: Int?
) {
    ALL("ALL", "전체", null),
    POEM("POEM", "시집", R.drawable.ic_category_poem_24dp),
    NOVEL("NOVEL", "소설", R.drawable.ic_category_novel_24dp),
    ESSAY("ESSAY", "에세이", R.drawable.ic_category_essay_24dp),
    CARTOON("CARTOON", "만화", R.drawable.ic_category_comic_24dp),
    PHOTO_BOOK("PHOTO_BOOK", "사진집", R.drawable.ic_category_photobook_24dp),
    ART_BOOK("ART_BOOK", "아트북", R.drawable.ic_category_artbook_24dp),
    ILLUSTRATION("ILLUSTRATION", "일러스트북", R.drawable.ic_category_illustrationbook_24dp),
    MAGAZINE("MAGAZINE", "매거진", R.drawable.ic_category_magazine_24dp);

    companion object {
        val BUSINESS_CATEGORIES: List<BookCategory> = entries.filter { it != ALL }
    }
}