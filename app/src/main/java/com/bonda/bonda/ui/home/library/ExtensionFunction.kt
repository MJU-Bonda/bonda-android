package com.bonda.bonda.ui.home.library

import android.content.res.Resources

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
