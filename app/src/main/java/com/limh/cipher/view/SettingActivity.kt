package com.limh.cipher.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import com.limh.cipher.R
import com.limh.cipher.base.AppConfig
import com.limh.cipher.base.BaseActivity
import com.limh.cipher.base.MyDialog
import com.limh.cipher.dao.Note
import com.limh.cipher.util.CacheUtils
import com.limh.cipher.util.FileUtils
import com.limh.cipher.util.FileUtils.getPath
import com.limh.cipher.util.FileUtils.getRealPathFromURI
import com.limh.cipher.util.Logs
import com.limh.cipher.util.encrypt.SM4Utils
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.File
import java.io.FileInputStream
import kotlin.concurrent.thread

/**
 * Function:
 * author: limh
 * time:2017/12/7
 */
class SettingActivity : BaseActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var key: String
    private val noteDao = AppConfig.getDefault().getDaoSession().noteDao
    private var dialog: ProgressDialog? = null

    override fun findViews() {
        setContentView(R.layout.activity_setting)
        imageSetBack.setOnClickListener(this)
        switchShow.isChecked = CacheUtils.get(this, "isShow", true) as Boolean
        switchShow.setOnCheckedChangeListener(this)
        lineSetKey.setOnClickListener(this)
        lineBackup.setOnClickListener(this)
        lineImport.setOnClickListener(this)

        key = CacheUtils.get(this, "key", "") as String
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageSetBack -> finish()
            R.id.lineSetKey -> {
                if (TextUtils.isEmpty(key)) {
                    setKey()
                } else {
                    resetDialog()
                }
            }
            R.id.lineBackup -> {
                fileBackup()
            }
            R.id.lineImport -> {
                importFile()
            }
        }
    }

    /**
     * 重置密钥对话框
     */
    private fun resetDialog() {
        val dialog = MyDialog(this)
        dialog.setContentView(R.layout.layout_dialog_input)
        dialog.setCanceledOnTouchOutside(false)
        dialog.findViewById<TextView>(R.id.txt_dialog_title).text = "修改密钥"
        val edit1 = dialog.findViewById<EditText>(R.id.edit_dialog_input1)
        edit1.hint = "旧密钥"
        val edit2 = dialog.findViewById<EditText>(R.id.edit_dialog_input2)
        edit2.hint = "新密钥"
        val edit3 = dialog.findViewById<EditText>(R.id.edit_dialog_input3)
        edit3.hint = "确认密钥"
        edit3.visibility = View.VISIBLE
        dialog.findViewById<TextView>(R.id.txt_dialog_cancel).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<TextView>(R.id.txt_dialog_yes).setOnClickListener {
            dialog.dismiss()
            if (edit1.text.toString().isEmpty() || edit2.text.toString().isEmpty() || edit3.text.toString().isEmpty()) {
                showShort("输入不能为空")
                return@setOnClickListener
            }
            if (edit1.text.toString() != key) {
                showShort("旧密钥不匹配")
                return@setOnClickListener
            }
            if (edit2.text.toString() != edit3.text.toString()) {
                showShort("新密钥两次输入不匹配")
                return@setOnClickListener
            }
            CacheUtils.put(this, "key", edit3.text.toString().trim())
            updatePass(edit3.text.toString().trim())
        }
        dialog.show()
    }

    /**
     * 没设置密钥前先设置密钥
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

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        CacheUtils.put(this, "isShow", isChecked)
    }

    /**
     * 根据新密钥 重新加密本地数据
     */
    private fun updatePass(newKey: String) {
        val list = noteDao.loadAll()
        if (list.size > 0) {
            for (item in list) {
                val orign = SM4Utils.decryptData_ECB(item.password, key)
                item.password = SM4Utils.encryptData_ECB(orign, newKey)
                noteDao.update(item)
            }
            showShort("更新成功")
        }
    }

    /**
     * 文件备份
     */
    private fun fileBackup() {
        val list = noteDao.loadAll()
        if (list.size <= 0) {
            showShort("本地数据为空，不可备份")
            return
        }
        val path = FileUtils.saveFile(FileUtils.gson2Str(list))
        val file = File(path)
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(this, "com.limh.cipher", file)
        } else {
            Uri.fromFile(file)
        }
        Logs.d(TAG,"分享路径：path="+path)
        Logs.d(TAG,"分享路径：uri="+uri.toString())
        val wechatIntent = Intent(Intent.ACTION_SEND)
        wechatIntent.type = "text/plain"
        wechatIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(wechatIntent, "发送到"))
    }

    private val FILE_SELECT_CODE = 0x21
    /**
     * 导入文件
     */
    private fun importFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        startActivityForResult(intent, FILE_SELECT_CODE)
    }

    private fun syncFile(path: String?) {
        val file = File(path)
        if (file.exists()) {
            handler.sendEmptyMessage(0x123)
            thread {
                val fileInput = FileInputStream(file)
                val b = ByteArray(fileInput.available())
                fileInput.read(b)
                val result = String(b)
                try {
                    val list = FileUtils.str2List(result, Note::class.java) as List<*>
                    val localList = noteDao.loadAll()
                    var isExit: Boolean
                    val total = list.size
                    var success = 0
                    for (item in list) {
                        val note: Note = item as Note
                        isExit = localList.any { note.username == it.username && note.password == it.password }
                        if (!isExit) {
                            noteDao.insert(note)
                            success++
                        }
                        Thread.sleep(100)
                    }
                    sendMsg("导入完成：总：" + total + "条，成功导入：" + success + "条", 0x125, handler)
                } catch (e: Exception) {
                    e.printStackTrace()
                    sendMsg("文件有误，仅支持app导出的文件", 0x125, handler)
                    handler.sendEmptyMessage(0x124)
                }
                handler.sendEmptyMessage(0x124)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FILE_SELECT_CODE -> {
                    val uri = data!!.data
                    val path = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        getPath(this, uri)
                    } else {
                        getRealPathFromURI(this, uri)
                    }
                    syncFile(path)
                    Logs.i(TAG, "DATA=" + path)
                }
            }
        }
    }

    private val handler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            0x123 -> {
                if (null == dialog) {
                    dialog = ProgressDialog(this)
                    dialog!!.setMessage("正在导入，请稍后...")
                }
                if (!dialog!!.isShowing) {
                    dialog!!.show()
                }
            }
            0x124 -> {
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            }
            0x125 -> {
                val bundle = msg.data
                showLong(bundle.getString("msg"))
            }
        }
        false
    })

    fun sendMsg(msg: String, what: Int, handler: Handler) {
        val bundle = Bundle()
        bundle.putString("msg", msg)
        val message = Message()
        message.what = what
        message.data = bundle
        handler.sendMessage(message)
    }
}