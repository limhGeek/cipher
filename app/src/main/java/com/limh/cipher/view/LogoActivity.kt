package com.limh.cipher.view

import android.os.Handler
import android.view.WindowManager
import com.limh.cipher.R
import com.limh.cipher.base.BaseActivity
import java.util.*

/**
 * Function:
 * author: limh
 * time:2017/12/11
 */
class LogoActivity : BaseActivity() {
    lateinit var timer: Timer
    override fun findViews() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_logo)
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.sendEmptyMessage(0x123)
            }
        }, 1000)
    }

    private val handler = Handler(Handler.Callback {
        openActivity(MainActivity::class.java)
        finish()
        false
    })
}