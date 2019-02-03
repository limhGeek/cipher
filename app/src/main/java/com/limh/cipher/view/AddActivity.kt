package com.limh.cipher.view

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.limh.cipher.R
import com.limh.cipher.base.AppConfig
import com.limh.cipher.base.BaseActivity
import com.limh.cipher.base.MyDialog
import com.limh.cipher.dao.Note
import com.limh.cipher.database.NoteDao
import com.limh.cipher.util.CacheUtils
import com.limh.cipher.util.Logs
import com.limh.cipher.util.Util
import com.limh.cipher.util.encrypt.SM4Utils
import com.limh.cipher.widget.wheelview.NumericWheelAdapter
import com.limh.cipher.widget.wheelview.OnWheelChangedListener
import com.limh.cipher.widget.wheelview.WheelView
import kotlinx.android.synthetic.main.activity_add.*

/**
 * Function:
 * author: limh
 * time:2017/12/6
 */
class AddActivity : BaseActivity(), View.OnClickListener {

    private val noteDao = AppConfig.getDefault().getDaoSession().noteDao
    private var group = "工作"
    private var id: Long? = null
    private var isEdit = false
    private var note: Note? = null

    lateinit var key: String
    override fun findViews() {
        setContentView(R.layout.activity_add)
        imageBack.setOnClickListener(this)
        imageAddBuild.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        txtAddClass.setOnClickListener(this)
        initDatas()
    }

    fun initDatas() {
        key = CacheUtils.get(this, "key", "") as String
        Logs.d("key=" + key)
        id = intent.getLongExtra("id", -1)
        Logs.i(TAG, "ID=" + id)
        if (null != id && (-1).toLong() != id) {
            note = noteDao.queryBuilder().where(NoteDao.Properties.Id.eq(id)).unique()
            Logs.i(TAG, "note=" + note.toString())
            if (null != note) {
                editAddTitle.setText(note!!.title)
                editAddUsername.setText(note!!.username)
                editAddPassword.setText(note!!.password)
                editAddContent.setText(note!!.content)
                txtAddClass.text = note!!.group
                editAddTitle.isEnabled = false
                editAddUsername.isEnabled = false
                editAddPassword.isEnabled = false
                editAddContent.isEnabled = false
                txtAddClass.isEnabled = false
                btnSave.visibility = View.GONE
                fabInfo.visibility = View.VISIBLE
                fabUpdate.setOnClickListener(this)
                fabDelete.setOnClickListener(this)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageBack -> {
                finish()
            }
            R.id.imageAddBuild -> {
                val intent = Intent()
                intent.setClass(this, BuildActivity::class.java)
                startActivityForResult(intent, 0x123)
            }
            R.id.btnSave -> {
                if (TextUtils.isEmpty(key)) {
                    setKey()
                    return
                }
                save()
            }
            R.id.txtAddClass -> {
                showClassDialog()
            }
            R.id.fabUpdate -> {
                isEdit = !isEdit
                if (isEdit) {
                    showKeyDialog()
                } else {
                    editAddTitle.isEnabled = false
                    editAddUsername.isEnabled = false
                    editAddPassword.isEnabled = false
                    editAddContent.isEnabled = false
                    txtAddClass.isEnabled = false
                    fabUpdate.setIcon(R.drawable.ic_edit)
                    note!!.title = editAddTitle.text.toString()
                    if (note!!.password != editAddPassword.text.toString()) {
                        note!!.password = SM4Utils.encryptData_ECB(editAddPassword.text.toString(), key)
                    }
                    note!!.username = editAddUsername.text.toString()
                    note!!.content = editAddContent.text.toString()
                    note!!.group = group
                    note!!.updateAt = Util.getDate(System.currentTimeMillis())
                    noteDao.update(note)
                }
            }
            R.id.fabDelete -> {
                noteDao.deleteByKey(id)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0x123 -> {
                if (null != data) {
                    editAddPassword.setText(data.getStringExtra("password"))
                }
            }
        }
    }

    /**
     * 保存
     */
    private fun save() {
        if (TextUtils.isEmpty(editAddPassword.text.toString())) {
            showLong("密码都没有，还怎么玩")
            return
        }
        val note = Note()
        note.title = editAddTitle.text.toString()
        note.password = SM4Utils.encryptData_ECB(editAddPassword.text.toString(), key)
        note.username = editAddUsername.text.toString()
        note.content = editAddContent.text.toString()
        note.group = group
        note.createAt = Util.getDate(System.currentTimeMillis())
        note.updateAt = Util.getDate(System.currentTimeMillis())
        noteDao.insert(note)
        showLong("添加成功")
        finish()
    }

    private fun showKeyDialog() {
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
            if (edit.text.toString() == key) {
                editAddTitle.isEnabled = true
                editAddUsername.isEnabled = true
                editAddPassword.isEnabled = true
                editAddContent.isEnabled = true
                txtAddClass.isEnabled = true
                fabUpdate.setIcon(R.drawable.ic_save)
            } else {
                isEdit = false
                fabUpdate.setIcon(R.drawable.ic_edit)
                showShort("密钥不正确")
            }
            dialog.dismiss()

        }
        dialog.show()
    }

    /**
     * 设置密钥
     */
    private fun setKey() {
        val inputDialog = MyDialog(this)
        inputDialog.setCanceledOnTouchOutside(false)
        inputDialog.setContentView(R.layout.layout_dialog_input)
        val edit1 = inputDialog.findViewById<EditText>(R.id.edit_dialog_input1)
        val edit2 = inputDialog.findViewById<EditText>(R.id.edit_dialog_input2)
        inputDialog.findViewById<TextView>(R.id.txt_dialog_cancel).setOnClickListener({ inputDialog.dismiss() })
        inputDialog.findViewById<TextView>(R.id.txt_dialog_yes).setOnClickListener({
            if (!TextUtils.isEmpty(edit1.text.toString()) && edit1.text.toString().equals(edit2.text.toString())) {
                CacheUtils.put(this, "key", edit1.text.toString())
                key = CacheUtils.get(this, "key", "") as String
                inputDialog.dismiss()
            } else {
                showLong("密钥不能为空")
            }
        })
        inputDialog.show()
    }

    private fun showClassDialog() {
        val classData = mutableListOf("工作", "生活")
        val classDialog = MyDialog(this)
        classDialog.setCanceledOnTouchOutside(false)
        classDialog.setContentView(R.layout.layout_choice_class)
        val wvClass = classDialog.findViewById<WheelView>(R.id.wvClass)
        wvClass.adapter = NumericWheelAdapter(classData)
        wvClass.isCyclic = false
        wvClass.addChangingListener(object : OnWheelChangedListener {
            override fun onChanged(context: View, oldValue: Int, newValue: Int) {
                group = classData[newValue]
                Logs.i("group=" + group)
            }
        })
        val btnYes = classDialog.findViewById<TextView>(R.id.btnYes)
        btnYes.setOnClickListener {
            txtAddClass.text = group
            classDialog.dismiss()
        }
        val btnCancel = classDialog.findViewById<TextView>(R.id.btnCancel)
        btnCancel.setOnClickListener { classDialog.dismiss() }
        classDialog.show()
    }
}