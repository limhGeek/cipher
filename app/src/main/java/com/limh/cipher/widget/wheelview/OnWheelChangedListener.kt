package com.limh.cipher.widget.wheelview

import android.view.View

/**
 * Created by limh on 2017/4/13.
 *
 */

interface OnWheelChangedListener {
    fun onChanged(context: View, oldValue: Int, newValue: Int)
}
