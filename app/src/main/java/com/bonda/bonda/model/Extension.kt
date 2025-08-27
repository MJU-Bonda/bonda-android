package com.bonda.bonda.model

import android.content.res.ColorStateList
import android.content.res.Resources
import androidx.core.content.ContextCompat
import com.bonda.bonda.R
import com.google.android.material.chip.Chip

/**
 * enum extensions
 */
fun String.toArticleCategory(): ArticleCategory =
    ArticleCategory.entries.find { it.code == this } ?: ArticleCategory.ALL

fun String.toBookCategory(): BookCategory =
    BookCategory.entries.find { it.code == this } ?: BookCategory.ALL

fun String.toBookTheme(): BookTheme =
    BookTheme.entries.find { it.code == this } ?: BookTheme.ALL

fun String.toSortOrder(): SortOrder =
    SortOrder.entries.find { it.code == this } ?: SortOrder.RECENTLY_SAVED

/**
 * dp를 px값으로 환산합니다
 */
fun Int.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}

/**
 * article category chip color extension
 * @param category 아티클의 카테고리값을 입력하세요
 */
fun Chip.setCategoryStyle(categoryCode: String) {
    val category = categoryCode.toArticleCategory()

    /**
     * enum 사전에 카테고리가 없는 경우 chip에 카테고리 code를 그대로 적용합니다
     */
    if (category == ArticleCategory.ALL)
        this.text = categoryCode
    else
        this.text = category.label

    val bgColorRes = when (category) {
        ArticleCategory.AUTHOR_OR_PUBLISHER -> R.color.surface_context_writer
        ArticleCategory.BOOKSTORE -> R.color.surface_context_store
        ArticleCategory.THEME -> R.color.surface_context_theme
        else -> R.color.surface_default_primary
    }
    val textColorRes = when (category) {
        ArticleCategory.AUTHOR_OR_PUBLISHER -> R.color.text_context_writer
        ArticleCategory.BOOKSTORE -> R.color.text_context_store
        ArticleCategory.THEME -> R.color.text_context_theme
        else -> R.color.text_accent_primary
    }

    this.chipBackgroundColor =
        ColorStateList.valueOf(ContextCompat.getColor(this.context, bgColorRes))
    this.setTextColor(ContextCompat.getColor(this.context, textColorRes))
}