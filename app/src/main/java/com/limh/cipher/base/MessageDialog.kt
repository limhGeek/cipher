package com.limh.cipher.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.limh.cipher.R

/**
 * Function:
 * author: limh
 * time:2017/11/24
 */

class MessageDialog : Dialog {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, themeResId: Int) : super(context, R.style.dialog) {

    }

    class Builder(private var context: Context?) {
        private var title: String? = null
        private var message: String? = null
        private var txtBtn1: String? = null
        private var txtBtn2: String? = null
        private var contentView: View? = null
        private var btn1OnClickListener: DialogInterface.OnClickListener? = null
        private var btn2OnClickListener: DialogInterface.OnClickListener? = null

        fun setContext(context: Context): Builder {
            this.context = context
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun setTxtBtn1(txtBtn1: String, listener: DialogInterface.OnClickListener): Builder {
            this.txtBtn1 = txtBtn1
            this.btn1OnClickListener = listener
            return this
        }

        fun setTxtBtn2(txtBtn2: String, listener: DialogInterface.OnClickListener): Builder {
            this.txtBtn2 = txtBtn2
            this.btn2OnClickListener = listener
            return this
        }

        fun setContentView(contentView: View): Builder {
            this.contentView = contentView
            return this
        }

        fun create(): MessageDialog {
            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialog = MessageDialog(context!!, R.style.dialog)
            dialog.setCanceledOnTouchOutside(false)
            @SuppressLint("InflateParams") val layout = inflater.inflate(R.layout.base_view_msg_dialog, null)
            dialog.addContentView(layout, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            if (!TextUtils.isEmpty(title)) {
                (layout.findViewById<View>(R.id.base_txt_dialog_title) as TextView).text = title
            }
            if (!TextUtils.isEmpty(txtBtn1)) {
                (layout.findViewById<View>(R.id.base_msg_dialog_btn1) as Button).text = txtBtn1
                if (null != btn1OnClickListener) {
                    layout.findViewById<View>(R.id.base_msg_dialog_btn1).setOnClickListener { btn1OnClickListener!!.onClick(dialog, DialogInterface.BUTTON_NEGATIVE) }
                }
            } else {
                layout.findViewById<View>(R.id.base_msg_dialog_btn1).visibility = View.GONE
                layout.findViewById<View>(R.id.base_msg_dialog_line).visibility = View.GONE
            }
            if (!TextUtils.isEmpty(txtBtn2)) {
                (layout.findViewById<View>(R.id.base_msg_dialog_btn2) as Button).text = txtBtn2
                if (null != btn2OnClickListener) {
                    layout.findViewById<View>(R.id.base_msg_dialog_btn2).setOnClickListener { btn2OnClickListener!!.onClick(dialog, DialogInterface.BUTTON_POSITIVE) }
                }
            }
            if (!TextUtils.isEmpty(message)) {
                (layout.findViewById<View>(R.id.base_msg_dialog_txt_msg) as TextView).text = message
            }
            dialog.setContentView(layout)
            return dialog
        }
    }

}
