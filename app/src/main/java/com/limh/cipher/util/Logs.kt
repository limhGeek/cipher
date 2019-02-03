package com.limh.cipher.util

import android.util.Log

/**
 * Function:
 * author: limh
 * time:2017/11/21
 */

object Logs {
    private var isDebug = true

    fun isEnableDebug(isDebug: Boolean) {
        Logs.isDebug = isDebug
    }

    fun i(tag: String, msg: String?) {
        if (isDebug) {
            Log.i(tag, msg ?: "")
        }
    }

    fun i(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.i(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    fun i(msg: String?) {
        if (isDebug) {
            Log.i(" [INFO] --- ", msg ?: "")
        }
    }

    fun d(tag: String, msg: String?) {
        if (isDebug) {
            Log.d(tag, msg ?: "")
        }
    }

    fun d(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.d(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    fun d(msg: String?) {
        if (isDebug) {
            Log.d(" [DEBUG] --- ", msg ?: "")
        }
    }

    fun w(tag: String, msg: String?) {
        if (isDebug) {
            Log.w(tag, msg ?: "")
        }
    }

    fun w(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.w(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    fun w(msg: String?) {
        if (isDebug) {
            Log.w(" [WARN] --- ", msg ?: "")
        }
    }

    fun e(tag: String, msg: String?) {
        if (isDebug) {
            Log.e(tag, msg ?: "")
        }
    }

    fun e(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.e(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    fun e(msg: String?) {
        if (isDebug) {
            Log.e(" [ERROR] --- ", msg ?: "")
        }
    }

    fun v(tag: String, msg: String?) {
        if (isDebug) {
            Log.v(tag, msg ?: "")
        }
    }

    fun v(`object`: Any, msg: String?) {
        if (isDebug) {
            Log.v(`object`.javaClass.simpleName, msg ?: "")
        }
    }

    fun v(msg: String?) {
        if (isDebug) {
            Log.v(" [VERBOSE] --- ", msg ?: "")
        }
    }
}
