package com.limh.readers.widget.recyclerview.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View

/**
 * Function:
 * author: limh
 * time:2017/7/26
 */
internal class StaggeredGridEntrust(leftRight: Int, topBottom: Int, mColor: Int) : SpacesItemDecorationEntrust(leftRight, topBottom, mColor) {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {}

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager as StaggeredGridLayoutManager
        val lp = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        val childPosition = parent.getChildAdapterPosition(view)
        val spanCount = layoutManager.spanCount
        val count = if (lp.isFullSpan) layoutManager.spanCount else 1

        if (layoutManager.orientation == GridLayoutManager.VERTICAL) {

            if (childPosition + count - 1 < spanCount) {//第一排的需要上面
                outRect.top = topBottom
            }
            if (lp.spanIndex + count == spanCount) {//最边上的需要右边,这里需要考虑到一个合并项的问题
                outRect.right = leftRight
            }
            outRect.bottom = topBottom
            outRect.left = leftRight

        } else {
            if (childPosition + count - 1 < spanCount) {//第一排的需要left
                outRect.left = leftRight
            }
            if (lp.spanIndex + count == spanCount) {//最边上的需要bottom
                outRect.bottom = topBottom
            }
            outRect.right = leftRight
            outRect.top = topBottom
        }
    }
}
