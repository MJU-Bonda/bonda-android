package com.bonda.bonda.network.model.book

import kotlinx.serialization.Serializable

@Serializable
data class NewBookRequest(
    val category: String,
    val categoryId: Long
)