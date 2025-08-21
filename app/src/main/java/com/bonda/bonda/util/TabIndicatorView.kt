package com.bonda.bonda.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children
import com.bonda.bonda.R

class TabIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    var dotSpacingDp: Int = 12
    var count: Int = 0
        private set

    init {
        orientation = HORIZONTAL
    }

    fun setCount(count: Int) {
        this.count = count
        removeAllViews()

        val size = dp(10)
        val margin = dp(dotSpacingDp) / 2

        val lp = LayoutParams(size, size).apply {
            setMargins(margin, 0, margin, 0)
        }

        repeat(count) {
            addView(View(context).apply {
                layoutParams = lp
                setBackgroundResource(R.drawable.indicator_dot_unselected)
                contentDescription = "dot_$it"
            })
        }
        select(0)
    }

    fun select(position: Int) {
        children.forEachIndexed { index, view ->
            view.setBackgroundResource(
                if (index == position) R.drawable.indicator_dot_selected
                else R.drawable.indicator_dot_unselected
            )
        }
    }

    /**
     * 적용할 spacing dp를 입력하세요
     */
    fun setSpacingDp(dp: Int) {
        dotSpacingDp = dp
        if (childCount > 0) setCount(count)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

}