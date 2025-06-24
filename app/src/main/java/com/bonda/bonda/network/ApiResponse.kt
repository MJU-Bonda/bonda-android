package com.bonda.bonda.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T
) {
    /**
     * api를 호출한 뒤 success 여부를 검사한 뒤 data를 unwrap하는 메서드
     */
    fun unwrapOrThrow(): T =
        if (success) data else throw ApiException("api call failed : success=false")
}

/**
 * exception 정의
 */
class ApiException(message: String) : RuntimeException(message)
