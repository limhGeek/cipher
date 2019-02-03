package com.limh.cipher.base

import android.app.Application
import com.limh.cipher.database.DaoMaster
import com.limh.cipher.database.DaoSession
import com.limh.cipher.database.SQLiteOperHelper
import com.limh.cipher.util.Logs
import kotlin.properties.Delegates

/**
 * Function:
 * author: limh
 * time:2017/12/5
 */
class AppConfig : Application() {

    companion object {
        var instance: AppConfig by Delegates.notNull()
        lateinit var daoSession: DaoSession
        fun getDefault(): AppConfig {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Logs.isEnableDebug(false)
        initDatabase()
    }

    private fun initDatabase() {
        val help = SQLiteOperHelper(this, "clipher.db", null)
        val daoMaster = DaoMaster(help.writableDb)
        daoSession = daoMaster.newSession()
    }

    fun getDaoSession(): DaoSession {
        return daoSession
    }
}