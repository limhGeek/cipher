package com.limh.readers.widget.recyclerview.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Function:
 * author: limh
 * time:2017/7/26
 */
class GridEntrust(leftRight: Int, topBottom: Int, mColor: Int) : SpacesItemDecorationEntrust(leftRight, topBottom, mColor) {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager as GridLayoutManager
        val lookup = layoutManager.spanSizeLookup

        if (mDivider == null || layoutManager.childCount == 0) {
            return
        }
        //判断总的数量是否可以整除
        val spanCount = layoutManager.spanCount

        var left: Int
        var right: Int
        var top: Int
        var bottom: Int

        val childCount = parent.childCount
        if (layoutManager.orientation == GridLayoutManager.VERTICAL) {

            for (i in 0..childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                //得到它在总数里面的位置
                val position = parent.getChildAdapterPosition(child)
                //获取它所占有的比重
                val spanSize = lookup.getSpanSize(position)
                //获取每排的位置
                val spanIndex = lookup.getSpanIndex(position, layoutManager.spanCount)
                //将带有颜色的分割线处于中间位置
                val centerLeft = ((layoutManager.getLeftDecorationWidth(child) + 1 - leftRight) / 2).toFloat()
                val centerTop = ((layoutManager.getBottomDecorationHeight(child) + 1 - topBottom) / 2).toFloat()
                //判断是否为第一排
                val isFirst = position + spanSize <= layoutManager.spanCount
                //画上边的，第一排不需要上边的,只需要在最左边的那项的时候画一次就好
                if (!isFirst && spanIndex == 0) {
                    left = layoutManager.getLeftDecorationWidth(child)
                    right = parent.width - layoutManager.getLeftDecorationWidth(child)

                    top = (child.top - centerTop).toInt() - topBottom
                    bottom = top + topBottom
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(c)
                }
                //最右边的一排不需要右边的
                val isRight = spanIndex + spanSize == spanCount
                if (!isRight) {
                    //计算右边的
                    left = (child.right + centerLeft).toInt()
                    right = left + leftRight
                    top = child.top

                    if (position + spanSize - 1 >= spanCount) {
                        top -= centerTop.toInt()
                    }
                    bottom = (child.bottom + centerTop).toInt()
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(c)
                }
            }
        } else {
            for (i in 0..childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                //得到它在总数里面的位置
                val position = parent.getChildAdapterPosition(child)
                //获取它所占有的比重
                val spanSize = lookup.getSpanSize(position)
                //获取每排的位置
                val spanIndex = lookup.getSpanIndex(position, layoutManager.spanCount)
                //将带有颜色的分割线处于中间位置
                val centerLeft = ((layoutManager.getRightDecorationWidth(child) + 1 - leftRight) / 2).toFloat()
                val centerTop = ((layoutManager.getTopDecorationHeight(child) + 1 - topBottom) / 2).toFloat()
                //判断是否为第一列
                val isFirst = position + spanSize <= layoutManager.spanCount
                //画左边的，第一排不需要左边的,只需要在最上边的那项的时候画一次就好
                if (!isFirst && spanIndex == 0) {
                    left = (child.left - centerLeft).toInt() - leftRight
                    right = left + leftRight

                    top = layoutManager.getRightDecorationWidth(child)
                    bottom = parent.height - layoutManager.getTopDecorationHeight(child)
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(c)
                }
                //最下的一排不需要下边的
                val isRight = spanIndex + spanSize == spanCount
                if (!isRight) {
                    //计算右边的
                    left = child.left
                    if (position + spanSize - 1 >= spanCount) {
                        left -= centerLeft.toInt()
                    }
                    right = (child.right + centerTop).toInt()

                    top = (child.bottom + centerLeft).toInt()
                    bottom = top + leftRight
                    mDivider!!.setBounds(left, top, right, bottom)
                    mDivider!!.draw(c)
                }
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager as GridLayoutManager
        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        val childPosition = parent.getChildAdapterPosition(view)
        val spanCount = layoutManager.spanCount

        if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
            if (childPosition + lp.spanSize - 1 < spanCount) {//第一排的需要上面
                outRect.top = topBottom
            }
            if (lp.spanIndex + lp.spanSize == spanCount) {//最边上的需要右边,这里需要考虑到一个合并项的问题
                outRect.right = leftRight
            }
            outRect.bottom = topBottom
            outRect.left = leftRight
        } else {
            if (childPosition + lp.spanSize - 1 < spanCount) {//第一排的需要left
                outRect.left = leftRight
            }
            if (lp.spanIndex + lp.spanSize == spanCount) {//最边上的需要bottom
                outRect.bottom = topBottom
            }
            outRect.right = leftRight
            outRect.top = topBottom
        }
    }


}
