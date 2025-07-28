package com.bonda.bonda.ui.home.library

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.roundToInt

class ShelfDecoration(
    context: Context,
    @DrawableRes shelfResId: Int,
    private val spanCount: Int,
    private val offsetFromRowBottomDp: Int = 24
) : RecyclerView.ItemDecoration() {

    private val shelfBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, shelfResId)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val offsetPx = offsetFromRowBottomDp.dp(context)
    private val dstRect = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val lm = parent.layoutManager as? GridLayoutManager ?: return

        // row(group)별 가장 아래 child의 bottom을 수집
        val rowBottomMap = HashMap<Int, Int>()
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val pos = parent.getChildAdapterPosition(child)
            if (pos == RecyclerView.NO_POSITION) continue

            val group = lm.spanSizeLookup.getSpanGroupIndex(pos, spanCount)
            val bottom = (child.bottom + child.translationY).toInt()
            rowBottomMap[group] = max(rowBottomMap[group] ?: Int.MIN_VALUE, bottom)
        }

        // 한 줄에 책이 하나라도 있으면 선반을 그림
        val parentWidth = parent.width - parent.paddingStart - parent.paddingEnd
        val scaledHeight = (shelfBitmap.height * (parentWidth.toFloat() / shelfBitmap.width)).toInt()

        for ((_, rowBottom) in rowBottomMap) {
            val top = rowBottom - offsetPx - scaledHeight
            dstRect.set(parent.paddingStart, top, parent.paddingStart + parentWidth, top + scaledHeight)
            c.drawBitmap(shelfBitmap, null, dstRect, paint)
        }
    }
}

// dp -> px
private fun Int.dp(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()
