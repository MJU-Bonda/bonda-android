package com.bonda.bonda.network.model.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToggleAutoSaveResponse(
    @SerialName("autoSave")
    val isAutoSaved: Boolean
)
