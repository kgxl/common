package com.kgxl.base

import android.app.Application
import android.content.Context

/**
 * Created by zjy on 2022/11/10
 */
class App : Application() {
    companion object{
        lateinit var application: Context
    }
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }
}