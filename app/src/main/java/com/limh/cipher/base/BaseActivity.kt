package com.limh.cipher.base

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import com.readystatesoftware.systembartint.SystemBarTintManager

/**
 * Function:
 * author: limh
 * time:2017/12/5
 */
abstract class BaseActivity : AppCompatActivity() {
    private var tintManager: SystemBarTintManager? = null

    lateinit var TAG: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = this.javaClass.simpleName
        initWindow()
        findViews()
    }

    abstract fun findViews()

    private fun initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            tintManager = SystemBarTintManager(this)
            val status = Color.parseColor("#1F96F2")
            tintManager!!.setStatusBarTintColor(status)
            tintManager!!.isStatusBarTintEnabled = true
        }
    }

    fun openActivity(cls: Class<*>) {
        val intent = Intent()
        intent.setClass(this, cls)
        startActivity(intent)
    }

    fun showLong(msg: String) = Toast.makeText(this.applicationContext, msg, Toast.LENGTH_LONG).show()
    fun showShort(msg: String) = Toast.makeText(this.applicationContext, msg, Toast.LENGTH_SHORT).show()
}