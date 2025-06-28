package com.bonda.bonda.network.model.search

import kotlinx.serialization.Serializable

@Serializable
data class GetSearchHistoryResponse(
    val recentSearchTermList: List<String>,
    val autoSave: Boolean
)