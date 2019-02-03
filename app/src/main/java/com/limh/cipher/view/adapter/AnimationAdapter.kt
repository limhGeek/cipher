package com.limh.cipher.view.adapter

import android.text.InputType
import android.widget.EditText
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.limh.cipher.R
import com.limh.cipher.dao.Note
import com.limh.cipher.util.Logs

/**
 * Function:
 * author: limh
 * time:2017/12/8
 */
class AnimationAdapter(data: MutableList<Note>) : BaseQuickAdapter<Note, BaseViewHolder>(R.layout.view_item_note, data) {
    var isShow = true
    private val itemBg = intArrayOf(R.drawable.shape_item_bg_1, R.drawable.shape_item_bg_2, R.drawable.shape_item_bg_3, R.drawable.shape_item_bg_4,
            R.drawable.shape_item_bg_5, R.drawable.shape_item_bg_6, R.drawable.shape_item_bg_7, R.drawable.shape_item_bg_8,
            R.drawable.shape_item_bg_9, R.drawable.shape_item_bg_10, R.drawable.shape_item_bg_11)

    override fun convert(helper: BaseViewHolder?, item: Note?) {
        Logs.d(TAG, "item:" + item!!.toString())
        if (!isShow) {
            helper!!.getView<EditText>(R.id.txtItemPass).inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            helper!!.getView<EditText>(R.id.txtItemPass).inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        helper.itemView.setBackgroundResource(itemBg[(Math.random() * 11).toInt()])
        helper.setText(R.id.txtItemTitle, item.title)
        helper.setText(R.id.txtItemName, item.username)
        helper.setText(R.id.txtItemPass, "" + item.password)
        helper.setText(R.id.txtItemContent, item.content)
        helper.setText(R.id.txtItemGroup, item.group)
    }
}