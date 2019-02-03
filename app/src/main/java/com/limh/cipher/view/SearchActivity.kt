package com.limh.cipher.view

import android.content.ClipData
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.limh.cipher.R
import com.limh.cipher.base.AppConfig
import com.limh.cipher.base.BaseActivity
import com.limh.cipher.base.MessageDialog
import com.limh.cipher.base.MyDialog
import com.limh.cipher.dao.Note
import com.limh.cipher.database.NoteDao
import com.limh.cipher.util.CacheUtils
import com.limh.cipher.util.DisplayUtil
import com.limh.cipher.util.Logs
import com.limh.cipher.util.encrypt.SM4Utils
import com.limh.cipher.view.adapter.AnimationAdapter
import com.limh.readers.widget.recyclerview.decoration.SpacesItemDecoration
import kotlinx.android.synthetic.main.activity_search.*

/**
 * Function:
 * author: limh
 * time:2017/12/12
 */
class SearchActivity : BaseActivity(), View.OnClickListener, TextWatcher {

    private val noteDao = AppConfig.instance.getDaoSession().noteDao
    private var currentList: MutableList<Note>? = null
    private var adapter: AnimationAdapter? = null
    private var isShow: Boolean = true

    override fun findViews() {
        setContentView(R.layout.activity_search)
        imageSearchBack.setOnClickListener(this)
        editSearch.addTextChangedListener(this)
        recycleSearch.setHasFixedSize(true)
        recycleSearch.layoutManager = LinearLayoutManager(this)
        recycleSearch.addItemDecoration(SpacesItemDecoration(DisplayUtil.dip2px(this, 10f), DisplayUtil.dip2px(this, 10f)))
        isShow = CacheUtils.get(this, "isShow", true) as Boolean
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageSearchBack -> {
                finish()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(s.toString())) {
            currentList = noteDao.queryBuilder().whereOr(NoteDao.Properties.Title.like("%" + s.toString() + "%"),
                    NoteDao.Properties.Username.like("%" + s.toString() + "%"),
                    NoteDao.Properties.Content.like("%" + s.toString() + "%")).list()
            Logs.i(TAG, "" + s.toString())
            Logs.i(TAG, "size:" + currentList!!.size)
        }else{
            currentList!!.clear()
        }
        updateUI()
    }

    private fun updateUI() {
        if (null == adapter) {
            adapter = AnimationAdapter(currentList!!)
            adapter!!.isShow = isShow
            adapter!!.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
            adapter!!.setNotDoAnimationCount(3)
            adapter!!.isFirstOnly(false)
            adapter!!.setOnItemLongClickListener { _, _, position ->
                showKeyDialog(currentList!![position])
                return@setOnItemLongClickListener true
            }
            adapter!!.setOnItemClickListener { _, _, position ->
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("id", currentList!![position].id)
                startActivity(intent)
            }
            recycleSearch.adapter = adapter
        } else {
            adapter!!.isShow = isShow
            adapter!!.setNewData(currentList)
        }
    }

    private fun showKeyDialog(note: Note) {
        Logs.i(TAG, "解密前：" + note.toString())
        val dialog = MyDialog(this)
        dialog.setContentView(R.layout.layout_dialog_input)
        dialog.setCanceledOnTouchOutside(false)
        dialog.findViewById<View>(R.id.edit_div3).visibility = View.GONE
        dialog.findViewById<View>(R.id.edit_div2).visibility = View.GONE
        dialog.findViewById<TextView>(R.id.txt_dialog_title).text = "验证密钥"
        val edit = dialog.findViewById<EditText>(R.id.edit_dialog_input1)
        dialog.findViewById<EditText>(R.id.edit_dialog_input2).visibility = View.GONE
        dialog.findViewById<TextView>(R.id.txt_dialog_cancel).setOnClickListener { dialog.cancel() }
        dialog.findViewById<TextView>(R.id.txt_dialog_yes).setOnClickListener {
            val temp = Note()

            val pass = SM4Utils.decryptData_ECB(note.password, edit.text.toString())
            if (TextUtils.isEmpty(pass)) {
                temp.title = "提示"
                temp.username = "密钥不正确，解密失败"
            } else {
                temp.title = note.title
                temp.username = note.username
                temp.password = pass
            }
            Logs.i(TAG, "解密后：" + note.toString())
            dialog.dismiss()
            showNoteInfo(temp)
        }
        dialog.show()
    }

    private fun showNoteInfo(note: Note) {
        val str: String
        val btn1: String
        val btn2: String
        if (TextUtils.isEmpty(note.password)) {
            str = note.username
            btn1 = "取消"
            btn2 = "确定"
        } else {
            str = "账号：" + note.username + "\n密码：" + note.password
            btn1 = "去分享"
            btn2 = "复制"
        }
        val dialog = MessageDialog.Builder(this)
                .setMessage(str)
                .setTitle(note.title)
                .setTxtBtn1(btn1, DialogInterface.OnClickListener { dialog, _ ->
                    if (!TextUtils.isEmpty(note.password)) {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_SUBJECT, note.title)
                        intent.putExtra(Intent.EXTRA_TEXT, str)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(Intent.createChooser(intent, "分享到"))
                    }
                    dialog.dismiss()
                })
                .setTxtBtn2(btn2, DialogInterface.OnClickListener { dialog, _ ->
                    if (!TextUtils.isEmpty(note.password)) {
                        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val cmdata = ClipData.newPlainText("label", str)
                        cm.primaryClip = cmdata
                        showShort("复制成功")
                    }
                    dialog.dismiss()
                })
                .create()
        dialog.show()
    }
}