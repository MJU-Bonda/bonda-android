package com.bonda.bonda.network.model.search

import kotlinx.serialization.Serializable

@Serializable
data class GetSearchHistoryRequest(
    val member: String
)
