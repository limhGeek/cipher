package com.limh.cipher.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Function:
 * author: limh
 * time:2017/12/6
 */

object Util {
    private val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val df1 = SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault())
    fun getDate(time: Long): String = df.format(Date(time))
    fun getFileDate(time: Long): String = df1.format(Date(time))
}
