package com.bonda.bonda.network.model.book

import kotlinx.serialization.Serializable

@Serializable
data class SaveBookResponse(
    val bookId: Long,
    val message: Message,
    val isNewBadge: Boolean
) {
    @Serializable
    data class Message(val message: String)
}
