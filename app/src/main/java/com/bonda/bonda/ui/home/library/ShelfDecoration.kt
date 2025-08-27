package com.bonda.bonda.ui.home.library

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

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

    // 1. 최소 선반 갯수와 한 행의 예상 높이를 정의합니다. (디자인에 맞게 조절 필요)
    private val minShelves = 3
    private val rowHeightPx = 160.dp(context) // 예시 높이, 실제 아이템 높이와 비슷하게 설정

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val lm = parent.layoutManager as? GridLayoutManager ?: return

        val rowBottomMap = HashMap<Int, Int>()
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val pos = parent.getChildAdapterPosition(child)
            if (pos == RecyclerView.NO_POSITION) continue

            val group = lm.spanSizeLookup.getSpanGroupIndex(pos, spanCount)
            val bottom = child.bottom
            rowBottomMap[group] = max(rowBottomMap[group] ?: Int.MIN_VALUE, bottom)
        }

        val parentWidth = parent.width - parent.paddingStart - parent.paddingEnd
        val scaledHeight = (shelfBitmap.height * (parentWidth.toFloat() / shelfBitmap.width)).toInt()

        val drawnShelves = mutableListOf<Int>()

        // 2. 아이템이 있는 행에 선반을 그립니다.
        for ((_, rowBottom) in rowBottomMap.toSortedMap()) {
            val top = rowBottom - offsetPx - scaledHeight
            dstRect.set(parent.paddingStart, top, parent.paddingStart + parentWidth, top + scaledHeight)
            c.drawBitmap(shelfBitmap, null, dstRect, paint)
            drawnShelves.add(rowBottom) // 그려진 선반의 y 좌표(bottom)를 기록
        }

        // 3. 그려진 선반 갯수가 최소 갯수보다 적을 경우, 빈 선반을 추가로 그립니다.
        var lastShelfBottom = drawnShelves.lastOrNull() ?: parent.paddingTop - offsetPx
        var shelvesToDraw = minShelves - drawnShelves.size

        while (shelvesToDraw > 0) {
            val nextShelfBottom = lastShelfBottom + rowHeightPx
            // RecyclerView 영역을 벗어나면 그리지 않습니다.
            if (nextShelfBottom > parent.height - parent.paddingBottom) break

            val top = nextShelfBottom - offsetPx - scaledHeight
            dstRect.set(parent.paddingStart, top, parent.paddingStart + parentWidth, top + scaledHeight)
            c.drawBitmap(shelfBitmap, null, dstRect, paint)

            lastShelfBottom = nextShelfBottom
            shelvesToDraw--
        }
    }
}

// dp -> px
private fun Int.dp(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()
