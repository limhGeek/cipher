package com.limh.cipher.widget.wheelview

/**
 * Created by limh on 2017/4/13.
 *
 */

class NumericWheelAdapter(private val datas: List<String>) : WheelAdapter {

    override val itemsCount: Int
        get() = datas.size

    override val maximumLength: Int
        get() = datas.size

    override fun getItem(index: Int): String {
        return datas[index]
    }
}
