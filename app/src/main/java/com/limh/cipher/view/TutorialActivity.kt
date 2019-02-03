package com.limh.cipher.view

import com.limh.cipher.R
import com.limh.cipher.base.BaseActivity
import kotlinx.android.synthetic.main.activity_tutorial.*

/**
 * Function:
 * author: limh
 * time:2017/12/8
 */
class TutorialActivity : BaseActivity() {
    override fun findViews() {
        setContentView(R.layout.activity_tutorial)
        imageBack.setOnClickListener { finish() }
    }
}