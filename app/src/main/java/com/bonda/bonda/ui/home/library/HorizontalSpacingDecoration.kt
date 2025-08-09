package com.bonda.bonda.ui.home.library

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpacingDecoration(
    context: Context,
    horizontalDp: Int,
    private val spanCount: Int
) : RecyclerView.ItemDecoration() {
    private val horizontalPx = (horizontalDp * context.resources.displayMetrics.density).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val column = position % spanCount
        when (column) {
            /**
             * 첫 번째 아이템의 왼쪽에 padding을 적용합니다
             */
            0 -> {
                outRect.left = horizontalPx
            }

            /**
             * 마지막 아이템의 오른쪽에 padding을 적용합니다
             */
            spanCount - 1 -> {
                outRect.right = horizontalPx
            }

            /**
             * 중간 아이템의 양쪽에 padding을 적용합니다
             */
            else -> {
                outRect.left = horizontalPx / 2
                outRect.right = horizontalPx / 2
            }
        }
    }
}