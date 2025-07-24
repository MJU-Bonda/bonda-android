package com.bonda.bonda.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class WithdrawalResponse(
    val message: String
)
