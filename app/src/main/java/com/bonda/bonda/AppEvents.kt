package com.bonda.bonda

import kotlinx.coroutines.flow.MutableSharedFlow

object AppEvents {
    val profileUpdated = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
}
