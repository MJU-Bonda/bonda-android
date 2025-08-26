package com.bonda.bonda.model

import kotlinx.coroutines.flow.MutableSharedFlow

object AppEvents {
    val homeArticlesUpdated = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val profileUpdated = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
}
