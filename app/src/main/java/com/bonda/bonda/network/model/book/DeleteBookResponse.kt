package com.bonda.bonda.network.model.book

import kotlinx.serialization.Serializable

@Serializable
data class DeleteBookResponse(
    val bookId: Long,
    val message: Message
) {
    @Serializable
    data class Message(val message: String)
}