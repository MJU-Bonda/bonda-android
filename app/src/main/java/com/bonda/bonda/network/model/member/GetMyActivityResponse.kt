package com.bonda.bonda.network.model.member

import kotlinx.serialization.Serializable

@Serializable
data class GetMyActivityResponse(
    val bookViewCount: Int,
    val bookcaseCount: Int,
    val categoryCountList: List<Category>
) {
    @Serializable
    data class Category(
        val category: String,
        val count: Int
    )
}