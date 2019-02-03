package com.limh.cipher.util

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.content.SharedPreferencesCompat

/**
 * Function:
 * author: limh
 * time:2017/12/6
 */
object CacheUtils {
    val FILE_NAME: String = "cipher"
    fun put(context: Context, key: String, any: Any) {
        val sp: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        val editer: SharedPreferences.Editor = sp.edit()

        if (any is String)
            editer.putString(key, any)
        else if (any is Int)
            editer.putInt(key, any)
        else if (any is Float)
            editer.putFloat(key, any)
        else if (any is Long)
            editer.putLong(key, any)
        else if (any is Boolean)
            editer.putBoolean(key, any)
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editer)
    }

    fun get(context: Context, key: String, default: Any): Any {
        val sp: SharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

        if (default is String)
            return sp.getString(key, default)
        else if (default is Int)
            return sp.getInt(key, default)
        else if (default is Float)
            return sp.getFloat(key, default)
        else if (default is Long)
            return sp.getLong(key, default)
        else if (default is Boolean)
            return sp.getBoolean(key, default)
        return ""
    }
}