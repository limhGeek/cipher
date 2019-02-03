package com.limh.cipher.view

import android.Manifest
import android.annotation.TargetApi
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.limh.cipher.util.FileUtils
import com.limh.cipher.util.Logs
import com.limh.cipher.util.QRCodeUtils.addLogo
import com.limh.cipher.util.QRCodeUtils.generateBitmap
import com.limh.cipher.util.QRCodeUtils.savePicture
import com.limh.cipher.util.encrypt.SM4Utils
import com.limh.cipher.view.adapter.AnimationAdapter
import com.limh.cipher.widget.PicassoTransformation
import com.limh.readers.widget.recyclerview.decoration.SpacesItemDecoration
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main.*
import java.io.File
import java.io.FileInputStream
import kotlin.concurrent.thread


class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var imagePhoto: ImageView
    private lateinit var txtSign: TextView

    private var isShow = true
    private var group: String = "全部"
    private var listNote: MutableList<Note>? = null
    private lateinit var noteDao: NoteDao
    private var adapter: AnimationAdapter? = null
    private var dialog: Dialog? = null
    private var dialog1: ProgressDialog? = null


    override fun findViews() {
        setContentView(R.layout.activity_main)
        initViews()
        checkPermissions()
        if (null != intent.data) {
            val uri: Uri = intent.data
            val filename = uri.path
            Logs.i(TAG, "导入的文件地址：" + filename)
            syncFile(filename)
        }
    }

    private fun initViews() {
        noteDao = AppConfig.getDefault().getDaoSession().noteDao
        listNote = mutableListOf()
        val headView = navigation.getHeaderView(0)
        imagePhoto = headView.findViewById(R.id.imagePhoto)
        txtSign = headView.findViewById(R.id.txtSign)

        navigation.setNavigationItemSelectedListener({ item: MenuItem ->
            when (item.itemId) {
                R.id.item_0,
                R.id.item_1,
                R.id.item_2 -> {
                    group = item.title.toString()
                    txtNav.text = group
                    Logs.i(TAG, group)
                    listNote = if ("全部" == group) {
                        noteDao.loadAll()
                    } else {
                        noteDao.queryBuilder().where(NoteDao.Properties.Group.eq(group)).list()
                    }
                    updateList()
                }
                R.id.item_3 -> {
                    dialog = Dialog(this, R.style.edit_AlertDialog_style)
                    dialog!!.setContentView(R.layout.dialog_view_arbigmap)
                    val imageAr = dialog!!.findViewById<ImageView>(R.id.imageAr)
                    val logoBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
                    imageAr.setImageBitmap(addLogo(generateBitmap("https://fir.im/qj4m", 400, 400)!!, logoBitmap))
                    val lineShare = dialog!!.findViewById<LinearLayout>(R.id.lineShare)
                    dialog!!.findViewById<TextView>(R.id.btnShareCancel).setOnClickListener { dialog!!.dismiss() }
                    dialog!!.findViewById<TextView>(R.id.btnShareYes).setOnClickListener {
                        lineShare.isDrawingCacheEnabled = true
                        lineShare.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                        lineShare.layout(0, 0, lineShare.measuredWidth, lineShare.measuredHeight)
                        val bitmap = Bitmap.createBitmap(lineShare.drawingCache)
                        lineShare.isDrawingCacheEnabled = false
                        shareImage(bitmap)
                        dialog!!.dismiss()
                    }

                    val w = dialog!!.window
                    val lp = w.attributes
                    lp.x = 0
                    lp.y = 40
                    dialog!!.onWindowAttributesChanged(lp)
                    imageAr.setOnClickListener({ _: View? -> dialog!!.dismiss() })
                    dialog!!.show()
                }
                R.id.item_4 -> {
                    openActivity(SettingActivity::class.java)
                }
                R.id.item_5 -> {
                    openActivity(TutorialActivity::class.java)
                }
            }
            //关闭侧滑
            if (drawerNv.isDrawerOpen(navigation)) {
                drawerNv.closeDrawer(navigation)
            }
            return@setNavigationItemSelectedListener true
        })
        navigation.itemIconTintList = null
        fabAdd.setOnClickListener(this)
        fabSearch.setOnClickListener(this)
        imageMenu.setOnClickListener(this)
        Picasso.with(this)
                .load(R.drawable.default_head)
                .transform(PicassoTransformation(0))
                .into(imagePhoto)
        recycle.setHasFixedSize(true)
        recycle.layoutManager = LinearLayoutManager(this)
        recycle.addItemDecoration(SpacesItemDecoration(DisplayUtil.dip2px(this, 10f), DisplayUtil.dip2px(this, 10f)))
    }

    override fun onResume() {
        super.onResume()
        isShow = CacheUtils.get(this, "isShow", true) as Boolean
        listNote = noteDao.loadAll()
        updateList()
    }

    private fun updateList() {
        if (listNote!!.size > 0) {
            lineNull.visibility = View.GONE
        } else {
            lineNull.visibility = View.VISIBLE
            imageNull.setImageResource(R.drawable.null_data)
        }
        if (null == adapter) {
            Logs.d(TAG, "更新界面：" + listNote!!.size)
            adapter = AnimationAdapter(listNote!!)
            adapter!!.isShow = isShow
            adapter!!.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
            adapter!!.setNotDoAnimationCount(3)
            adapter!!.isFirstOnly(true)
            adapter!!.setOnItemLongClickListener { _, _, position ->
                showKeyDialog(listNote!![position])
                return@setOnItemLongClickListener true
            }
            adapter!!.setOnItemClickListener { _, _, position ->
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("id", listNote!![position].id)
                startActivity(intent)
            }
            recycle.adapter = adapter
        } else {
            adapter!!.isShow = isShow
            adapter!!.setNewData(listNote)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageMenu -> {
                if (drawerNv.isDrawerOpen(navigation)) {
                    drawerNv.closeDrawer(navigation)
                } else {
                    drawerNv.openDrawer(navigation)
                }
            }
            R.id.fabAdd -> {
                openActivity(AddActivity::class.java)
            }
            R.id.fabSearch -> {
                openActivity(SearchActivity::class.java)
            }
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

    /**
     * 分享图片
     */
    private fun shareImage(bitmap: Bitmap) {
        savePicture(bitmap, "cipher.jpeg")
        val wechatIntent = Intent(Intent.ACTION_SEND)
        wechatIntent.type = "image/jpeg"
        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/Download/cipher.jpeg")
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(this, "com.limh.cipher", file)
        } else {
            Uri.fromFile(file)
        }
        wechatIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(wechatIntent, "分享"))
    }

    /**
     * Android6.0以上动态获取权限
     */
    private val PERMISSION_READ_STORAGE = 0
    private val PERMISSION_WRITE_STORAGE = 1

    @TargetApi(value = 23)
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_READ_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_WRITE_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_READ_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showLong("缺少读SD卡权限")
            }
            PERMISSION_WRITE_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showLong("缺少写SD卡权限")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private var extTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (drawerNv.isDrawerOpen(navigation)) {
            drawerNv.closeDrawer(navigation)
            return false
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - extTime) > 2000) {
                showShort("再按一次退出程序")
                extTime = System.currentTimeMillis()
            } else {
                finish()
                System.exit(0)
            }
        }
        return true
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
                    Logs.d(TAG,"导入出错："+e.printStackTrace())
                    sendMsg("文件有误，仅支持app导出的文件", 0x125, handler)
                    handler.sendEmptyMessage(0x124)
                }
                handler.sendEmptyMessage(0x124)
            }
        }
    }

    private val handler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            0x123 -> {
                if (null == dialog1) {
                    dialog1 = ProgressDialog(this)
                    dialog1!!.setMessage("正在导入，请稍后...")
                }
                if (!dialog1!!.isShowing) {
                    dialog1!!.show()
                }
            }
            0x124 -> {
                if (dialog1!!.isShowing) {
                    dialog1!!.dismiss()
                }
            }
            0x125 -> {
                val bundle = msg.data
                showLong(bundle.getString("msg"))
                listNote = noteDao.loadAll()
                updateList()
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