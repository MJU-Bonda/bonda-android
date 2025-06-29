package com.bonda.bonda.ui.profile.history

import androidx.lifecycle.ViewModel

class RecentBooksViewModel : ViewModel() {




}

data class RecentBook (
    val id: Int,
    val imageUrl: String,
    val category: String,
    val title: String,
    val subtitle: String
)

data class RecentArticle (
    val id: Int,
    val imageUrl: String,
    val category: String,
    val title: String,
)