package com.bonda.bonda.ui.home.library

import android.content.Context

private fun Int.dp(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()
