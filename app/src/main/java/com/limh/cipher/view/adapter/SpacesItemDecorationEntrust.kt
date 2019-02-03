package com.limh.readers.widget.recyclerview.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Function:
 * author: limh
 * time:2017/7/26
 */

abstract class SpacesItemDecorationEntrust(protected var leftRight: Int, protected var topBottom: Int, mColor: Int) {

    //color的传入方式是resouce.getcolor
    protected var mDivider: Drawable?=null

    init {
        if (mColor != 0) {
            mDivider = ColorDrawable(mColor)
        }
    }


    internal abstract fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State)

    internal abstract fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)

}
